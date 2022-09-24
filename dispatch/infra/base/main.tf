terraform {
  required_version = "1.2.8"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.30"
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

module "db" {
  source      = "./modules/db"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
}

module "api" {
  source      = "./modules/api"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
  db_host     = module.db.aurora_postgresql_v2_cluster_endpoint
  db_name     = module.db.aurora_postgresql_v2_cluster_database_name
  db_port     = module.db.aurora_postgresql_v2_cluster_port
  db_pass     = module.db.aurora_postgresql_v2_cluster_master_password
  db_user     = module.db.aurora_postgresql_v2_cluster_master_username
}

module "ui" {
  source      = "./modules/ui"
  env         = var.env
  domain_name = var.domain_name
  app_name    = var.app_name
}

# outputs

output "db_host" { value = module.db.aurora_postgresql_v2_cluster_endpoint }
output "db_name" { value = module.db.aurora_postgresql_v2_cluster_database_name }
output "db_port" { value = module.db.aurora_postgresql_v2_cluster_port }

output "ui_bucket_name" { value = module.ui.bucket_name }
output "ui_distribution_id" { value = module.ui.distribution_id }
