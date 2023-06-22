variable "domain_name" {}

module "site" {
  source      = "./site"
  domain_name = var.domain_name
}
