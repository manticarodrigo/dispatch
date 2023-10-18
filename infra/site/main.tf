locals {
  domain_name     = "${var.app_name}.${var.domain_name}"
  bucket_name     = "${var.app_name}-site-${terraform.workspace}"
  origin_name     = "${var.app_name}-origin-${terraform.workspace}"
  api_origin_name = "${var.app_name}-api-origin-${terraform.workspace}"
}

# Route53

data "aws_route53_zone" "main" {
  name = var.domain_name
}

resource "aws_route53_record" "site_cert_dns" {
  allow_overwrite = true
  name            = tolist(aws_acm_certificate.site_cert.domain_validation_options)[0].resource_record_name
  records         = [tolist(aws_acm_certificate.site_cert.domain_validation_options)[0].resource_record_value]
  type            = tolist(aws_acm_certificate.site_cert.domain_validation_options)[0].resource_record_type
  zone_id         = data.aws_route53_zone.main.zone_id
  ttl             = 60
}

resource "aws_route53_record" "site_a" {
  zone_id = data.aws_route53_zone.main.zone_id
  name    = local.domain_name
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.site_s3_dist.domain_name
    zone_id                = aws_cloudfront_distribution.site_s3_dist.hosted_zone_id
    evaluate_target_health = false
  }
}

# ACM

resource "aws_acm_certificate" "site_cert" {
  domain_name       = local.domain_name
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "site_cert_validate" {
  certificate_arn         = aws_acm_certificate.site_cert.arn
  validation_record_fqdns = [aws_route53_record.site_cert_dns.fqdn]
}

# S3
resource "aws_s3_bucket" "site_bucket" {
  bucket        = local.bucket_name
  force_destroy = true
}

resource "null_resource" "site_sync" {
  triggers = {
    sha1 = var.sha1
  }

  provisioner "local-exec" {
    command     = <<-EOT
                  zip -r app.zip *
                  aws s3 sync . s3://${aws_s3_bucket.site_bucket.id}
                  aws cloudfront create-invalidation --distribution-id ${aws_cloudfront_distribution.site_s3_dist.id} --paths "/*"
                EOT
    working_dir = "../dispatch/public"
  }

  depends_on = [var.build]
}

data "aws_iam_policy_document" "site_policy" {
  statement {
    actions = [
      "s3:GetObject",
    ]
    principals {
      identifiers = ["*"]
      type        = "AWS"
    }
    resources = [
      aws_s3_bucket.site_bucket.arn,
      "${aws_s3_bucket.site_bucket.arn}/*",
    ]
  }
}

resource "aws_s3_bucket_policy" "site_policy" {
  bucket = aws_s3_bucket.site_bucket.id
  policy = data.aws_iam_policy_document.site_policy.json
}

resource "aws_s3_bucket_acl" "site_acl" {
  bucket = aws_s3_bucket.site_bucket.id
  acl    = "public-read"
}

resource "aws_s3_bucket_website_configuration" "site_config" {
  bucket = aws_s3_bucket.site_bucket.bucket

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

# Cloudfront

resource "aws_cloudfront_distribution" "site_s3_dist" {
  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"
  aliases             = [local.domain_name]

  origin {
    domain_name = aws_s3_bucket.site_bucket.bucket_domain_name
    origin_id   = local.origin_name
  }

  origin {
    domain_name = replace(var.api_invoke_url, "/^https?://([^/]*).*/", "$1")
    origin_id   = local.api_origin_name

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD", "OPTIONS"]
    target_origin_id = local.origin_name

    forwarded_values {
      query_string = true

      cookies {
        forward = "all"
      }
    }

    max_ttl                = 86400
    default_ttl            = 3600
    min_ttl                = 0
    viewer_protocol_policy = "redirect-to-https"
  }

  ordered_cache_behavior {
    path_pattern     = var.api_stage_name
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD", "OPTIONS"]
    target_origin_id = local.api_origin_name

    min_ttl                = 0
    default_ttl            = 0
    max_ttl                = 0
    compress               = true
    viewer_protocol_policy = "redirect-to-https"


    cache_policy_id          = data.aws_cloudfront_cache_policy.site.id
    origin_request_policy_id = data.aws_cloudfront_origin_request_policy.site_api.id
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    acm_certificate_arn      = aws_acm_certificate.site_cert.arn
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1"
  }

  custom_error_response {
    error_code         = 404
    response_code      = 200
    response_page_path = "/index.html"
  }
}

data "aws_cloudfront_origin_request_policy" "site_api" {
  name = "Managed-CORS-CustomOrigin"
}

data "aws_cloudfront_cache_policy" "site" {
  name = "Managed-CachingOptimized"
}
