locals {
  app_name     = "dispatch"
  version_name = "0.0.4"
}

variable "domain_name" {}

module "vpc" {
  source = "./vpc"
}

module "db" {
  source   = "./db"
  app_name = local.app_name
  vpc_id   = module.vpc.vpc_id
}

module "build" {
  source               = "./build"
  domain_name          = var.domain_name
  app_name             = local.app_name
  version_name         = local.version_name
  rum_monitor_id       = module.site.rum_monitor_id
  rum_identity_pool_id = module.site.rum_identity_pool_id
  rum_guest_role_arn   = module.site.rum_guest_role_arn
}

module "api" {
  source           = "./api"
  domain_name      = var.domain_name
  app_name         = local.app_name
  version_name     = local.version_name
  sha1             = module.build.sha1
  build            = module.build.build
  db_host          = module.db.host
  db_name          = module.db.name
  db_port          = module.db.port
  db_user          = module.db.username
  db_pass          = module.db.password
  site_bucket_name = module.site.site_bucket_name
}

module "site" {
  source         = "./site"
  domain_name    = var.domain_name
  app_name       = local.app_name
  sha1           = module.build.sha1
  build          = module.build.build
  api_invoke_url = module.api.api_invoke_url
  api_stage_name = module.api.api_stage_name
}
