locals {
  domain_name = "api.${var.app_name}.${var.domain_name}"
}


# Route53

data "aws_route53_zone" "main" {
  name = var.domain_name
}

resource "aws_route53_record" "api_cert_dns" {
  allow_overwrite = true
  name            = tolist(aws_acm_certificate.api_cert.domain_validation_options)[0].resource_record_name
  records         = [tolist(aws_acm_certificate.api_cert.domain_validation_options)[0].resource_record_value]
  type            = tolist(aws_acm_certificate.api_cert.domain_validation_options)[0].resource_record_type
  zone_id         = data.aws_route53_zone.main.zone_id
  ttl             = 60
}

resource "aws_route53_record" "api_a" {
  name    = aws_apigatewayv2_domain_name.api.domain_name
  type    = "A"
  zone_id = data.aws_route53_zone.main.zone_id

  alias {
    name                   = aws_apigatewayv2_domain_name.api.domain_name_configuration[0].target_domain_name
    zone_id                = aws_apigatewayv2_domain_name.api.domain_name_configuration[0].hosted_zone_id
    evaluate_target_health = false
  }
}

# ACM

resource "aws_acm_certificate" "api_cert" {
  domain_name       = local.domain_name
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "api_cert_validate" {
  certificate_arn         = aws_acm_certificate.api_cert.arn
  validation_record_fqdns = [aws_route53_record.api_cert_dns.fqdn]
}

# S3

resource "aws_s3_bucket" "api" {
  bucket = "${var.app_name}-lambda-${var.env}"
}

resource "aws_s3_bucket_acl" "api" {
  bucket = aws_s3_bucket.api.id
  acl    = "private"
}

resource "null_resource" "build" {
  triggers = {
    updated_at = timestamp()
  }

  provisioner "local-exec" {
    command     = "yarn && yarn release"
    working_dir = "${path.module}/../../dispatch"
  }
}

data "archive_file" "api" {
  type        = "zip"
  source_dir  = "${path.module}/../../dispatch/out"
  output_path = "${path.module}/app.zip"
  depends_on  = [null_resource.build]
}

resource "aws_s3_object" "api" {
  bucket = aws_s3_bucket.api.id
  key    = "app.zip"
  source = data.archive_file.api.output_path
  etag   = filemd5(data.archive_file.api.output_path)
}

# Lambda

resource "aws_lambda_function" "api" {
  function_name = "${var.app_name}-api-${var.env}"

  s3_bucket = aws_s3_bucket.api.id
  s3_key    = aws_s3_object.api.key

  runtime       = "nodejs16.x"
  architectures = ["arm64"]
  handler       = "api.handler"
  timeout       = 10

  source_code_hash = data.archive_file.api.output_base64sha256

  role = aws_iam_role.api.arn

  environment {
    variables = {
      "STAGE"      = var.env
      "PGHOST"     = var.db_host
      "PGDATABASE" = var.db_name
      "PGPORT"     = var.db_port
      "PGUSER"     = var.db_user
      "PGPASSWORD" = var.db_pass
    }
  }
}

resource "aws_cloudwatch_log_group" "api-lambda" {
  name = "/aws/lambda/${aws_lambda_function.api.function_name}"

  retention_in_days = 30
}

resource "aws_iam_role" "api" {
  name = "${var.app_name}-api-${var.env}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Sid    = ""
      Principal = {
        Service = "lambda.amazonaws.com"
      }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "api" {
  role       = aws_iam_role.api.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

# API Gateway

resource "aws_apigatewayv2_api" "api" {
  name          = "${var.app_name}-api-${var.env}"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = ["*"]
    allow_methods = ["POST", "GET", "OPTIONS"]
    allow_headers = ["content-type"]
    max_age       = 300
  }
}

resource "aws_apigatewayv2_stage" "api" {
  api_id = aws_apigatewayv2_api.api.id

  name        = "${var.app_name}-api-stage"
  auto_deploy = true

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api-gw.arn

    format = jsonencode({
      requestId               = "$context.requestId"
      sourceIp                = "$context.identity.sourceIp"
      requestTime             = "$context.requestTime"
      protocol                = "$context.protocol"
      httpMethod              = "$context.httpMethod"
      resourcePath            = "$context.resourcePath"
      routeKey                = "$context.routeKey"
      status                  = "$context.status"
      responseLength          = "$context.responseLength"
      integrationErrorMessage = "$context.integrationErrorMessage"
      }
    )
  }
}

resource "aws_apigatewayv2_integration" "api" {
  api_id = aws_apigatewayv2_api.api.id

  integration_uri    = aws_lambda_function.api.invoke_arn
  integration_type   = "AWS_PROXY"
  integration_method = "POST"
}

resource "aws_apigatewayv2_route" "api" {
  api_id = aws_apigatewayv2_api.api.id

  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.api.id}"
}

resource "aws_apigatewayv2_domain_name" "api" {
  domain_name = local.domain_name

  domain_name_configuration {
    certificate_arn = aws_acm_certificate.api_cert.arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}

resource "aws_apigatewayv2_api_mapping" "api" {
  api_id      = aws_apigatewayv2_api.api.id
  stage       = aws_apigatewayv2_stage.api.id
  domain_name = aws_apigatewayv2_domain_name.api.id
}

resource "aws_cloudwatch_log_group" "api-gw" {
  name = "/aws/api_gw/${aws_apigatewayv2_api.api.name}"

  retention_in_days = 30
}

resource "aws_lambda_permission" "api" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.api.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_apigatewayv2_api.api.execution_arn}/*/*"
}