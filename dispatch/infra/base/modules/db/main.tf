data "aws_rds_engine_version" "postgresql" {
  engine  = "aurora-postgresql"
  version = "13.6"
}

module "db" {
  source = "terraform-aws-modules/rds-aurora/aws"

  name              = "${var.app_name}-db-${var.env}"
  engine            = data.aws_rds_engine_version.postgresql.engine
  engine_mode       = "provisioned"
  engine_version    = data.aws_rds_engine_version.postgresql.version
  storage_encrypted = true

  vpc_id                = var.vpc_id
  subnets               = var.subnets
  allowed_cidr_blocks   = var.cidr_blocks
  create_security_group = true

  database_name   = "main"
  master_username = "root"

  apply_immediately   = true
  skip_final_snapshot = true

  serverlessv2_scaling_configuration = {
    min_capacity = 1
    max_capacity = 2
  }

  instance_class = "db.serverless"
  instances = {
    writer = {}
    reader = {}
  }
}
