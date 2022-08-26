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
variable "aws_account_id" {}
variable "aws_region" {}
variable "datomic_ami" {}
variable "datomic_license" {}

module "vpc" {
  source = "./modules/vpc"
}

module "datomic" {
  source          = "./modules/datomic"
  env             = var.env
  aws_account_id  = var.aws_account_id
  aws_region      = var.aws_region
  datomic_ami     = var.datomic_ami
  datomic_license = var.datomic_license
  subnets         = module.vpc.database_subnets
  vpc_id          = module.vpc.vpc_id
  vpc_ip_block    = module.vpc.vpc_cidr_block
}

module "ui" {
  source = "./modules/ui"
  env    = var.env
}
