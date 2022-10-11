locals {
  identifier = "${var.app_name}-db"
}

resource "aws_security_group" "lambda" {
  vpc_id = var.vpc_id
  name   = "${local.identifier}-lambda-sg-${var.env}"

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "proxy" {
  vpc_id = var.vpc_id
  name   = "${local.identifier}-proxy-sg-${var.env}"

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.lambda.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "db" {
  vpc_id = var.vpc_id
  name   = "${local.identifier}-sg-${var.env}"

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    security_groups = [aws_security_group.proxy.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "master" {
  identifier     = "${local.identifier}-${var.env}"
  engine         = "postgres"
  engine_version = "13.7"
  instance_class = "db.t4g.micro"

  allocated_storage     = 5
  max_allocated_storage = 100

  skip_final_snapshot = true
  publicly_accessible = true

  vpc_security_group_ids = [aws_security_group.db.id]

  db_name  = "main"
  username = "root"
  password = "temp1234"

  maintenance_window      = "Mon:00:00-Mon:03:00"
  backup_window           = "03:00-06:00"
  backup_retention_period = 1
}

# resource "aws_db_instance" "replica" {
#   identifier          = "${local.identifier}-replica-${var.env}"
#   replicate_source_db = aws_db_instance.master.identifier
#   instance_class      = aws_db_instance.master.instance_class
#   allocated_storage   = aws_db_instance.master.allocated_storage
#   engine              = aws_db_instance.master.engine
#   engine_version      = aws_db_instance.master.engine_version

#   skip_final_snapshot = true
#   publicly_accessible = true

#   vpc_security_group_ids = [aws_security_group.db.id]

#   db_name     = aws_db_instance.master.name
#   username = ""
#   password = ""

#   backup_retention_period = 0
# }
