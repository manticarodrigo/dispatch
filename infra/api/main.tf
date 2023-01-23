locals {
  db_map = {
    prod = "prisma://aws-us-east-1.prisma-data.com/?api_key=uaDQmuSFUZ2bYuT7ISDagl5hERUSzx67MawH1gPHWfvLwX1hQI_HeuWKI5X1sBoN"
    dev  = "prisma://aws-us-east-1.prisma-data.com/?api_key=Dvlt7xJcZo681KS2Ro5UnyEwoSx-V1jdVZDU3ESY6rVBzBYx-wGVst1cPBhwDKqV"
  }
  concurrency_map = {
    prod = 1
    dev  = 1
  }
  domain_name    = "api.${var.app_name}.${var.domain_name}"
  db_url         = local.db_map[terraform.workspace]
  db_migrate_url = "postgresql://${var.db_user}:${var.db_pass}@${var.db_host}:${var.db_port}/${var.db_name}"
  api_name       = "${var.app_name}-api-${terraform.workspace}"
  api_stage_name = "${var.app_name}-api-stage-${terraform.workspace}"
}

data "aws_region" "current" {}

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
  bucket = "${var.app_name}-api-${terraform.workspace}"
}

resource "aws_s3_bucket_acl" "api" {
  bucket = aws_s3_bucket.api.id
  acl    = "private"
}

resource "null_resource" "api_sync" {
  triggers = {
    sha1 = var.sha1
  }

  provisioner "local-exec" {
    command     = <<-EOT
                  zip -r api.zip *
                  aws s3 cp api.zip s3://${aws_s3_bucket.api.id}/api.zip
                EOT
    working_dir = "../dispatch/dist"
  }

  depends_on = [var.build]
}

resource "null_resource" "api_lambda_sync" {
  triggers = {
    "sha1" = var.sha1
  }

  provisioner "local-exec" {
    command = <<-EOT
              version=$(aws lambda update-function-code \
                --function-name ${aws_lambda_function.api.function_name} \
                --s3-bucket ${aws_s3_bucket.api.id} \
                --s3-key api.zip \
                --publish \
                --query Version \
                --output text)

                status="Pending"

                while [ "$status" = "Pending" ]
                  do
                    status=$(aws lambda get-function-configuration \
                      --function-name ${aws_lambda_function.api.function_name} \
                      --query State \
                      --output text)
                    echo "Current status: $status"
                    sleep 5
                done

                echo "Lambda function version $version is now $status"

              aws lambda update-alias \
                --function-name ${aws_lambda_function.api.function_name} \
                --name ${aws_lambda_alias.api.name} \
                --function-version $version \
                --routing-config AdditionalVersionWeights={}
            EOT
  }

  depends_on = [
    null_resource.api_sync
  ]
}

resource "null_resource" "api_db_sync" {
  triggers = {
    "sha1" = var.sha1
  }

  provisioner "local-exec" {
    command     = <<-EOT
                  export DATABASE_URL=${nonsensitive(local.db_migrate_url)}
                  yarn migrate
                EOT
    working_dir = "../dispatch"
  }

  depends_on = [
    null_resource.api_lambda_sync
  ]
}

# Lambda

resource "aws_lambda_function" "api" {
  function_name = "${var.app_name}-api-${terraform.workspace}"

  runtime       = "nodejs16.x"
  architectures = ["arm64"]
  memory_size   = 256
  handler       = "index.handler"
  timeout       = 10

  s3_bucket = aws_s3_bucket.api.id
  s3_key    = "api.zip"

  role = aws_iam_role.api.arn

  tracing_config {
    mode = "Active"
  }

  layers = [
    "arn:aws:lambda:us-east-1:580247275435:layer:LambdaInsightsExtension-Arm64:2"
  ]

  environment {
    variables = {
      STAGE        = terraform.workspace
      DATABASE_URL = local.db_url
    }
  }
}

resource "aws_lambda_alias" "api" {
  name             = "${var.app_name}-api-alias-${terraform.workspace}"
  function_name    = aws_lambda_function.api.arn
  function_version = aws_lambda_function.api.version
}

resource "aws_lambda_provisioned_concurrency_config" "api" {
  function_name                     = aws_lambda_function.api.function_name
  provisioned_concurrent_executions = local.concurrency_map[terraform.workspace]
  qualifier                         = aws_lambda_alias.api.name
}

resource "aws_iam_role" "api" {
  name = "${var.app_name}-api-${terraform.workspace}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = ""
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "execution_policy" {
  role       = aws_iam_role.api.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "insights_policy" {
  role       = aws_iam_role.api.id
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchLambdaInsightsExecutionRolePolicy"
}

data "aws_iam_policy" "lambda_xray" {
  name = "AWSXRayDaemonWriteAccess"
}

resource "aws_iam_role_policy_attachment" "aws_xray_write_only_access" {
  role       = aws_iam_role.api.name
  policy_arn = data.aws_iam_policy.lambda_xray.arn
}

# API Gateway

resource "aws_apigatewayv2_api" "api" {
  name          = local.api_name
  protocol_type = "HTTP"
  cors_configuration {
    allow_origins = ["*"]
    allow_methods = ["OPTIONS", "GET", "POST"]
    allow_headers = ["content-type", "authorization", "x-amzn-trace-id"]
    max_age       = 86400
  }
}

resource "aws_apigatewayv2_stage" "api" {
  name        = local.api_stage_name
  api_id      = aws_apigatewayv2_api.api.id
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

  default_route_settings {
    detailed_metrics_enabled = true
    throttling_burst_limit   = 5000
    throttling_rate_limit    = 10000
  }
}

resource "aws_apigatewayv2_integration" "api" {
  api_id = aws_apigatewayv2_api.api.id

  integration_uri    = aws_lambda_alias.api.invoke_arn
  integration_type   = "AWS_PROXY"
  integration_method = "POST"
}

resource "aws_apigatewayv2_route" "get" {
  api_id = aws_apigatewayv2_api.api.id

  route_key = "GET /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.api.id}"
}

resource "aws_apigatewayv2_route" "post" {
  api_id = aws_apigatewayv2_api.api.id

  route_key = "POST /{proxy+}"
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

resource "aws_lambda_permission" "api" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.api.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.api.execution_arn}/*/*"
  qualifier     = aws_lambda_alias.api.name
}

# Monitoring

resource "aws_cloudwatch_log_group" "api-lambda" {
  name = "/aws/lambda/${aws_lambda_function.api.function_name}"

  retention_in_days = 30
}

resource "aws_cloudwatch_log_group" "api-gw" {
  name = "/aws/api_gw/${aws_apigatewayv2_api.api.name}"

  retention_in_days = 30
}
