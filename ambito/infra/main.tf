variable "domain_name" {}

module "build" {
  source      = "./build"
  domain_name = var.domain_name
}

module "site" {
  source      = "./site"
  domain_name = var.domain_name
  build       = module.build.build
  sha1        = module.build.sha1
}
