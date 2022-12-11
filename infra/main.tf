terraform {
  required_version = "1.3.1"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.33.0"
    }
    archive = {
      source  = "hashicorp/archive"
      version = "2.2.0"
    }
  }

  backend "s3" {
    bucket = "ambito-infra-state"
    key = "ambito/terraform.tfstate"
    region = "us-east-1"
    workspace_key_prefix = "env"
  }
}

provider "aws" {
  allowed_account_ids = [var.aws_account_id]
  region              = var.aws_region
}

# variables

variable "aws_account_id" {}
variable "aws_region" {}

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
  source         = "./db"
  region         = var.aws_region
  app_name       = var.app_name
  vpc_id         = module.vpc.vpc_id
  vpc_subnet_ids = module.vpc.default_subnet_ids
}

module "build" {
  source      = "./build"
  domain_name = local.domain_name
  app_name    = local.app_name
}

module "api" {
  source                   = "./api"
  sha1                     = module.build.sha1
  build                    = module.build.build
  region                   = var.aws_region
  account_id               = var.aws_account_id
  domain_name              = var.domain_name
  app_name                 = var.app_name
  db_host                  = module.db.host
  db_name                  = module.db.name
  db_port                  = module.db.port
  db_user                  = module.db.username
  db_pass                  = module.db.password
  lambda_security_group_id = module.db.lambda_security_group_id
  proxy_resource_id        = module.db.proxy_resource_id
  vpc_subnet_ids           = module.vpc.default_subnet_ids
}

module "site" {
  source      = "./site"
  sha1        = module.build.sha1
  build       = module.build.build
  domain_name = local.domain_name
  app_name    = local.app_name
}
