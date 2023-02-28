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
    datadog = {
      source  = "DataDog/datadog"
      version = "3.18.0"
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
}

module "ambito" {
  source      = "../ambito/infra"
  domain_name = local.domain_name
}

module "dispatch" {
  source      = "../dispatch/infra"
  domain_name = local.domain_name
}
