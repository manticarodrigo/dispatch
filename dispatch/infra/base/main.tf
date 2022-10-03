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
    bucket = "dispatch-infra-state"
  }
}

provider "aws" {
  allowed_account_ids = [var.aws_account_id]
  region              = var.aws_region
}

# variables

variable "env" {}
variable "aws_account_id" {}
variable "aws_region" {}
variable "domain_name" {
  default = "ambito.app"
}
variable "app_name" {
  default = "dispatch"
}

# modules

module "vpc" {
  source   = "./modules/vpc"
  env      = var.env
  region   = var.aws_region
  app_name = var.app_name
}

module "db" {
  source      = "./modules/db"
  env         = var.env
  app_name    = var.app_name
  vpc_id      = module.vpc.vpc_id
  subnets     = module.vpc.database_subnets
  cidr_blocks = module.vpc.database_subnets_cidr_blocks
}

module "api" {
  source             = "./modules/api"
  env                = var.env
  domain_name        = var.domain_name
  app_name           = var.app_name
  subnets            = module.vpc.database_subnets
  security_group_ids = [module.vpc.security_group_id, module.db.cluster_security_group_id]
  db_host            = module.db.cluster_endpoint
  db_name            = module.db.cluster_database_name
  db_port            = module.db.cluster_port
  db_pass            = module.db.cluster_master_password
  db_user            = module.db.cluster_master_username
}

module "ui" {
  source      = "./modules/ui"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
}

# outputs

output "ui_bucket_name" { value = module.ui.bucket_name }
output "ui_distribution_id" { value = module.ui.distribution_id }
