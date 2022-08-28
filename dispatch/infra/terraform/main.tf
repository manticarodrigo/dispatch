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

module "network" {
  source = "./modules/network"
  env    = var.env
}

module "database" {
  source          = "./modules/database"
  env             = var.env
  aws_account_id  = var.aws_account_id
  aws_region      = var.aws_region
  datomic_ami     = var.datomic_ami
  datomic_license = var.datomic_license
  subnets         = module.network.database_subnets
  vpc_id          = module.network.vpc_id
  vpc_ip_block    = module.network.vpc_cidr_block
}

module "website" {
  source = "./modules/website"
  env    = var.env
}

module "domain" {
  source                                 = "./modules/domain"
  env                                    = var.env
  vpc_id                                 = module.network.vpc_id
  cloudfront_distribution_domain_name    = module.website.domain_name
  cloudfront_distribution_hosted_zone_id = module.website.zone_id
}
