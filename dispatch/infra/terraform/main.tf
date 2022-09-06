terraform {
  required_version = "1.2.8"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.27"
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

variable "env" {}
variable "app_name" {
  default = "dispatch"
}
variable "aws_account_id" {}
variable "aws_region" {}

module "ecr" {
  source   = "./modules/ecr"
  env      = var.env
  app_name = var.app_name
}

module "eks" {
  source   = "./modules/eks"
  env      = var.env
  app_name = var.app_name
}

module "website" {
  source   = "./modules/website"
  env      = var.env
  app_name = var.app_name
}
