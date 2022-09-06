locals {
  domain      = "ambito.app"
  bucket_name = "${var.app_name}-site-${var.env}"
  origin_name = "${var.app_name}-origin-${var.env}"
  subdomain   = "${var.app_name}.${local.domain}"
}

# S3
resource "aws_s3_bucket" "site_bucket" {
  bucket        = local.bucket_name
  force_destroy = true
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

resource "aws_s3_bucket_public_access_block" "site_bucket_access_control" {
  bucket             = aws_s3_bucket.site_bucket.id
  block_public_acls  = true
  ignore_public_acls = true
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
resource "aws_cloudfront_distribution" "s3_dist" {
  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"
  aliases             = [local.subdomain]

  origin {
    domain_name = aws_s3_bucket.site_bucket.bucket_domain_name
    origin_id   = local.origin_name
  }

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = local.origin_name

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    max_ttl                = 86400
    default_ttl            = 3600
    min_ttl                = 0
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    acm_certificate_arn      = "arn:aws:acm:us-east-1:420328682924:certificate/704ae56b-10b4-4b18-a389-6269ec8ea0d7"
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1"
  }

  custom_error_response {
    error_code         = 404
    response_code      = 200
    response_page_path = "/index.html"
  }
}


# Route53

data "aws_route53_zone" "main" {
  name = local.domain
}

resource "aws_route53_zone" "subdomain" {
  name = local.subdomain
}

resource "aws_route53_record" "subdomain-ns" {
  zone_id = aws_route53_zone.main.zone_id
  name    = local.subdomain
  type    = "NS"
  ttl     = "30"
  records = aws_route53_zone.subdomain.name_servers
}

resource "aws_route53_record" "subdomain-a" {
  zone_id = data.aws_route53_zone.main.zone_id
  name    = local.subdomain
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.s3_dist.domain_name
    zone_id                = aws_cloudfront_distribution.s3_dist.hosted_zone_id
    evaluate_target_health = false
  }
}
