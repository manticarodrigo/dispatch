data "aws_rds_engine_version" "postgresql" {
  engine  = "aurora-postgresql"
  version = "13.6"
}

module "db" {
  source = "terraform-aws-modules/rds-aurora/aws"

  name           = "${var.app_name}-db-${var.env}"
  engine         = data.aws_rds_engine_version.postgresql.engine
  engine_version = data.aws_rds_engine_version.postgresql.version

  vpc_id                = var.vpc_id
  subnets               = var.subnets
  allowed_cidr_blocks   = var.cidr_blocks
  create_security_group = true

  database_name   = "main"
  master_username = "root"

  apply_immediately   = true
  skip_final_snapshot = true

  enabled_cloudwatch_logs_exports = ["postgresql"]

  instances = {
    1 = {
      instance_class      = "db.t2.micro"
      publicly_accessible = true
    }
  }
}
