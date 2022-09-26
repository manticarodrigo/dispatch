resource "aws_rds_cluster" "db" {
  cluster_identifier = "${var.app_name}-db-cluster-${var.env}"
  engine             = "aurora-postgresql"
  engine_mode        = "provisioned"
  engine_version     = "13.6"
  database_name      = "main"
  master_username    = "test"
  master_password    = "test1234"

  serverlessv2_scaling_configuration {
    max_capacity = 1.0
    min_capacity = 0.5
  }
}

resource "aws_rds_cluster_instance" "db" {
  cluster_identifier  = aws_rds_cluster.db.id
  instance_class      = "db.serverless"
  engine              = aws_rds_cluster.db.engine
  engine_version      = aws_rds_cluster.db.engine_version
  publicly_accessible = true
}
