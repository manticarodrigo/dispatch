data "aws_route53_zone" "zone" {
  name = "ambito.app"
}

resource "aws_route53_record" "subdomain-a" {
  zone_id = data.aws_route53_zone.zone.zone_id
  name    = "dispatch.ambito.app"
  type    = "A"

  alias {
    name                   = var.cloudfront_distribution_domain_name
    zone_id                = var.cloudfront_distribution_hosted_zone_id
    evaluate_target_health = false
  }
}
