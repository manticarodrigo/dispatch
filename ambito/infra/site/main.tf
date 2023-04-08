locals {
  redirect_target = "https://dispatch.${var.domain_name}"
}

resource "aws_s3_bucket" "redirect_bucket" {
  bucket = var.domain_name
  acl    = "public-read"

  website {
    redirect_all_requests_to = local.redirect_target
  }
}

resource "aws_route53_zone" "example" {
  name = var.domain_name
}

resource "aws_route53_record" "apex_domain_A" {
  zone_id = aws_route53_zone.example.zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = aws_s3_bucket.redirect_bucket.website_domain
    zone_id                = aws_s3_bucket.redirect_bucket.hosted_zone_id
    evaluate_target_health = false
  }
}
