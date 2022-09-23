locals {
  name   = "${var.app_name}-db"
  region = "us-east-1"
}

data "aws_rds_engine_version" "postgresql" {
  engine  = "aurora-postgresql"
  version = "13.6"
}

module "aurora_postgresql_v2" {
  source = "terraform-aws-modules/rds-aurora/aws"

  name              = "${local.name}-postgresqlv2-${var.env}"
  engine            = data.aws_rds_engine_version.postgresql.engine
  engine_mode       = "provisioned"
  engine_version    = data.aws_rds_engine_version.postgresql.version
  storage_encrypted = true

  vpc_id                = module.vpc.vpc_id
  subnets               = module.vpc.database_subnets
  create_security_group = true
  allowed_cidr_blocks   = module.vpc.private_subnets_cidr_blocks

  monitoring_interval = 60

  apply_immediately   = true
  skip_final_snapshot = true

  db_parameter_group_name         = aws_db_parameter_group.db.id
  db_cluster_parameter_group_name = aws_rds_cluster_parameter_group.db.id

  serverlessv2_scaling_configuration = {
    min_capacity = 2
    max_capacity = 10
  }

  instance_class = "db.serverless"
  instances = {
    one = {}
    two = {}
  }
}

resource "aws_db_parameter_group" "db" {
  name        = "${local.name}-aurora-db-postgres13-parameter-group-${var.env}"
  family      = "aurora-postgresql13"
  description = "${local.name}-aurora-db-postgres13-parameter-group-${var.env}"
}

resource "aws_rds_cluster_parameter_group" "db" {
  name        = "${local.name}-aurora-postgres13-cluster-parameter-group-${var.env}"
  family      = "aurora-postgresql13"
  description = "${local.name}-aurora-postgres13-cluster-parameter-group-${var.env}"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 3.0"

  name = "${local.name}-${var.env}"
  cidr = "10.99.0.0/18"

  enable_dns_support   = true
  enable_dns_hostnames = true

  azs              = ["${local.region}a", "${local.region}b", "${local.region}c"]
  public_subnets   = ["10.99.0.0/24", "10.99.1.0/24", "10.99.2.0/24"]
  private_subnets  = ["10.99.3.0/24", "10.99.4.0/24", "10.99.5.0/24"]
  database_subnets = ["10.99.7.0/24", "10.99.8.0/24", "10.99.9.0/24"]

  enable_nat_gateway = false
}
