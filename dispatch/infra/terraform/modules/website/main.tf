locals {
  bucket_name = "dispatch-site-${var.env}"
}

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

resource "aws_cloudfront_distribution" "s3_dist" {
  origin {
    domain_name = aws_s3_bucket.site_bucket.bucket_domain_name
    origin_id   = "dispatch-site-origin-${var.env}"
  }

  # aliases = ["dispatch.ambito.app"]

  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["HEAD", "DELETE", "POST", "GET", "OPTIONS", "PUT", "PATCH"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "dispatch-site-origin-${var.env}"

    forwarded_values {
      query_string = false

      cookies {
        forward = "all"
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
    cloudfront_default_certificate = true
  }

  custom_error_response {
    error_code         = 404
    response_code      = 200
    response_page_path = "/index.html"
  }
}
