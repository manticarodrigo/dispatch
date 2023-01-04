terraform {
  required_version = "1.3.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.46.0"
    }
    archive = {
      source  = "hashicorp/archive"
      version = "2.2.0"
    }
  }

  backend "s3" {
    bucket               = "ambito-infra-state"
    key                  = "ambito/terraform.tfstate"
    region               = "us-east-1"
    workspace_key_prefix = "env"
  }
}

locals {
  domain_map = {
    prod = "ambito.app"
    dev  = "ambito.dev"
  }
  domain_name = local.domain_map[terraform.workspace]
  app_name    = "dispatch"
}

# modules

module "vpc" {
  source = "./vpc"
}

module "db" {
  source   = "./db"
  app_name = local.app_name
  vpc_id   = module.vpc.vpc_id
}

module "build" {
  source      = "./build"
  domain_name = local.domain_name
  app_name    = local.app_name
}

module "api" {
  source          = "./api"
  sha1            = module.build.sha1
  build           = module.build.build
  domain_name     = local.domain_name
  app_name        = local.app_name
  db_host         = module.db.host
  db_name         = module.db.name
  db_port         = module.db.port
  db_user         = module.db.username
  db_pass         = module.db.password
}

module "site" {
  source         = "./site"
  sha1           = module.build.sha1
  build          = module.build.build
  domain_name    = local.domain_name
  app_name       = local.app_name
  api_invoke_url = module.api.api_invoke_url
  api_stage_name = module.api.api_stage_name
}
