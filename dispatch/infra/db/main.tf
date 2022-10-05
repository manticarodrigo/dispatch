locals {
  cluster_identifier = "${var.app_name}-db-${var.env}"
}
resource "aws_rds_cluster" "cluster" {
  cluster_identifier = local.cluster_identifier
  engine             = "aurora-postgresql"
  availability_zones = ["${var.region}a", "${var.region}b", "${var.region}c"]
  database_name      = "main"
  master_username    = "root"
  master_password    = "temp1234"
}

resource "aws_rds_cluster_instance" "cluster_instances" {
  count               = 1
  identifier          = "${local.cluster_identifier}-${count.index}"
  cluster_identifier  = aws_rds_cluster.cluster.id
  engine              = aws_rds_cluster.cluster.engine
  engine_version      = aws_rds_cluster.cluster.engine_version
  instance_class      = "db.t2.micro"
  publicly_accessible = true
  apply_immediately   = true

}
