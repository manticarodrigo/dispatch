terraform {
  required_version = "1.2.8"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.27"
    }
    archive = {
      source  = "hashicorp/archive"
      version = "~> 2.2.0"
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
variable "domain_name" {
  default = "ambito.app"
}
variable "app_name" {
  default = "dispatch"
}
variable "aws_account_id" {}
variable "aws_region" {}

# modules

module "api" {
  source   = "./modules/api"
  env      = var.env
  app_name = var.app_name
}

module "ui" {
  source      = "./modules/ui"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
}

output "api_function_name" {
  value = module.api.function_name
}

output "ui_bucket_name" {
  value = module.ui.bucket_name
}

output "ui_distribution_id" {
  value = module.ui.distribution_id
}
