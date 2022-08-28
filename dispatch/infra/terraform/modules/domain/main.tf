locals {
  zone_name      = sort(keys(module.zones.route53_zone_zone_id))[0]
  domain_name    = "ambito.app"
  subdomain_name = "dispatch"
}

module "zones" {
  source = "terraform-aws-modules/route53/aws//modules/zones"

  zones = {
    "${local.domain_name}" = {
      comment = "${local.domain_name}"
      tags = {
        Name = local.domain_name
      }
    }

    "${local.subdomain_name}.${local.domain_name}" = {
      comment           = "${local.subdomain_name}.${local.domain_name}"
      delegation_set_id = module.delegation_sets.route53_delegation_set_id.main
      tags = {
        Name = "${local.subdomain_name}.${local.domain_name}"
      }
    }
  }
}

module "records" {
  source = "terraform-aws-modules/route53/aws//modules/records"

  zone_name = local.zone_name

  records = [
    {
      name = ""
      type = "A"
      ttl  = 3600
      records = [
        "10.10.10.10",
      ]
    },
    {
      name = "cloudfront"
      type = "A"
      alias = {
        name    = var.cloudfront_distribution_domain_name
        zone_id = var.cloudfront_distribution_hosted_zone_id
      }
    },
    {
      name = "cloudfront"
      type = "AAAA"
      alias = {
        name    = var.cloudfront_distribution_domain_name
        zone_id = var.cloudfront_distribution_hosted_zone_id
      }
    },
    {
      name            = "failover-primary"
      type            = "A"
      set_identifier  = "failover-primary"
      health_check_id = aws_route53_health_check.failover.id
      alias = {
        name    = var.cloudfront_distribution_domain_name
        zone_id = var.cloudfront_distribution_hosted_zone_id
      }
      failover_routing_policy = {
        type = "PRIMARY"
      }
    },
    {
      name           = "latency-test"
      type           = "A"
      set_identifier = "latency-test"
      alias = {
        name                   = var.cloudfront_distribution_domain_name
        zone_id                = var.cloudfront_distribution_hosted_zone_id
        evaluate_target_health = true
      }
      latency_routing_policy = {
        region = "us-east-1"
      }
    }
  ]

  depends_on = [module.zones]
}

module "records_with_full_names" {
  source = "terraform-aws-modules/route53/aws//modules/records"

  zone_name = local.zone_name

  records = [
    {
      name               = "with-full-name-override.${local.zone_name}"
      full_name_override = true
      type               = "A"
      ttl                = 3600
      records = [
        "10.10.10.10",
      ]
    },
    {
      name = "web"
      type = "A"
      ttl  = 3600
      records = [
        "10.10.10.11",
        "10.10.10.12",
      ]
    },
  ]

  depends_on = [module.zones]
}

module "delegation_sets" {
  source = "terraform-aws-modules/route53/aws//modules/delegation-sets"

  delegation_sets = {
    main = {}
  }
}

module "disabled_records" {
  source = "terraform-aws-modules/route53/aws//modules/records"

  create = false
}

resource "aws_route53_health_check" "failover" {
  fqdn              = var.cloudfront_distribution_domain_name
  port              = 443
  type              = "HTTPS"
  resource_path     = "/index.html"
  failure_threshold = 3
  request_interval  = 30
}
