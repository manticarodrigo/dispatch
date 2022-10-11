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
  source = "./vpc"
}

module "db" {
  source   = "./db"
  env      = var.env
  region   = var.aws_region
  app_name = var.app_name
  vpc_id   = module.vpc.vpc_id
}

module "api" {
  source      = "./api"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
  db_host     = module.db.host
  db_name     = module.db.name
  db_port     = module.db.port
  db_pass     = module.db.password
  db_user     = module.db.username
}

module "ui" {
  source      = "./ui"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
}

# outputs

output "ui_bucket_name" { value = module.ui.bucket_name }
output "ui_distribution_id" { value = module.ui.distribution_id }
