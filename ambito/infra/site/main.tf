locals {
  subdomain = "dispatch.${var.domain_name}"
}

data "aws_route53_zone" "main" {
  name = var.domain_name
}

resource "aws_iam_role" "lambda_edge" {
  name = "lambda-edge"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_lambda_function" "redirect" {
  function_name    = "redirect"
  filename         = "lambda_redirect.zip"
  source_code_hash = filebase64sha256("lambda_redirect.zip")
  handler          = "lambda_redirect.handler"
  role             = aws_iam_role.lambda_edge.arn
  runtime          = "nodejs14.x"
  publish          = true
}

resource "aws_lambda_permission" "allow_cloudfront" {
  statement_id  = "AllowExecutionFromCloudFront"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.redirect.function_name
  principal     = "edgelambda.amazonaws.com"

  source_arn = "arn:aws:cloudfront::*:distribution/*"
}

resource "aws_s3_bucket" "apex_domain_bucket" {
  bucket = var.domain_name
}

resource "aws_cloudfront_distribution" "s3_distribution" {
  origin {
    domain_name = aws_s3_bucket.apex_domain_bucket.bucket_regional_domain_name
    origin_id   = "S3-Bucket"

    s3_origin_config {
      origin_access_identity = "origin-access-identity/cloudfront/EXAMPLE" # Replace with your CloudFront Origin Access Identity
    }
  }

  enabled             = true
  is_ipv6_enabled     = true
  comment             = "CloudFront Distribution with Lambda@Edge for redirect"
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-Bucket"

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400

    lambda_function_association {
      event_type = "viewer-request"
      lambda_arn = "${aws_lambda_function.redirect.arn}:${aws_lambda_function.redirect.version}"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }
}

resource "aws_route53_record" "apex_domain_A" {
  zone_id = data.aws_route53_zone.main.zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.s3_distribution.domain_name
    zone_id                = aws_cloudfront_distribution.s3_distribution.hosted_zone_id
    evaluate_target_health = false
  }
}

output "cloudfront_domain" {
  value = aws_cloudfront_distribution.s3_distribution.domain_name
}
