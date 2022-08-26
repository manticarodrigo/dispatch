locals {
  system_name = "dispatch-${var.env}"
}

# transactor role. ec2 instances can assume the role of a transactor
resource "aws_security_group" "datomic_inbound" {
  name        = "${local.system_name}_datomic_inbound"
  description = "Allow access to Datomic Transactor"

  vpc_id = var.vpc_id

  tags = {
    Name = "${local.system_name}_transactors"
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 4334
    to_port     = 4334
    protocol    = "tcp"
    cidr_blocks = ["${var.vpc_ip_block}"]
  }

  egress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_iam_role" "transactor" {
  name = "${local.system_name}-transactor"

  assume_role_policy = <<EOF
{
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Sid": ""
    }
  ],
  "Version": "2012-10-17"
}
EOF
}

# policy with write access to cloudwatch
resource "aws_iam_role_policy" "transactor_cloudwatch" {
  name = "cloudwatch_access"
  role = aws_iam_role.transactor.id

  policy = <<EOF
{
  "Statement": [
    {
      "Action": [
        "cloudwatch:PutMetricData",
        "cloudwatch:PutMetricDataBatch"
      ],
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "true"
        }
      },
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}

# s3 bucket for the transactor logs
resource "aws_s3_bucket" "transactor_logs" {
  bucket        = "${local.system_name}-datomic-logs"
  force_destroy = true

  lifecycle {
    create_before_destroy = true
  }
}

# policy with write access to the transactor logs
resource "aws_iam_role_policy" "transactor_logs" {
  name = "s3_logs_access"
  role = aws_iam_role.transactor.id

  policy = <<EOF
{
  "Statement": [
    {
      "Action": [
        "s3:PutObject"
      ],
      "Effect": "Allow",
      "Resource": [
        "arn:aws:s3:::${aws_s3_bucket.transactor_logs.id}",
        "arn:aws:s3:::${aws_s3_bucket.transactor_logs.id}/*"
      ]
    }
  ]
}
EOF
}

# policy with complete access to the dynamodb table
resource "aws_iam_role_policy" "transactor" {
  name  = "dynamo_access"
  role  = aws_iam_role.transactor.id
  count = 1

  policy = <<EOF
{
  "Statement": [
    {
      "Action": [
        "dynamodb:*"
      ],
      "Effect": "Allow",
      "Resource": "arn:aws:dynamodb:*:${var.aws_account_id}:table/${var.dynamodb_table_id}"
    }
  ]
}
EOF
}

# instance profile which assumes the transactor role
resource "aws_iam_instance_profile" "transactor" {
  name = "${local.system_name}-datomic-transactor"
  role = aws_iam_role.transactor.name
}

# transactor launch config
resource "aws_launch_configuration" "transactor" {
  name_prefix          = "${local.system_name}-datomic-transactor-"
  image_id             = var.datomic_ami
  instance_type        = var.transactor_instance_type
  iam_instance_profile = aws_iam_instance_profile.transactor.name
  security_groups      = ["${aws_security_group.datomic_inbound.id}"]

  user_data = templatefile("${path.module}/scripts/run-transactor.sh", {
    xmx                    = "${var.transactor_xmx}"
    java_opts              = "${var.transactor_java_opts}"
    region                 = "${var.aws_region}"
    transactor_role        = "${aws_iam_role.transactor.name}"
    memory_index_max       = "${var.transactor_memory_index_max}"
    s3_log_bucket          = "${aws_s3_bucket.transactor_logs.id}"
    memory_index_threshold = "${var.transactor_memory_index_threshold}"
    object_cache_max       = "${var.transactor_object_cache_max}"
    license-key            = "${var.datomic_license}"
    cloudwatch_dimension   = "${var.cloudwatch_dimension}"
    memcached_uri          = "${var.memcached_uri}"

    protocol = "ddb"

    # For SQL only:
    sql_user     = "${var.sql_user}"
    sql_password = "${var.sql_password}"
    sql_url      = "${var.sql_url}"

    # For Dynamo only:
    aws_dynamodb_table  = "${var.dynamodb_table_id}"
    aws_dynamodb_region = "${var.aws_region}"
  })

  associate_public_ip_address = true

  ephemeral_block_device {
    device_name  = "/dev/sdb"
    virtual_name = "ephemeral0"
  }
  lifecycle {
    create_before_destroy = true
  }
}

# autoscaling group for launching transactors
resource "aws_autoscaling_group" "datomic_asg" {
  name                 = "${local.system_name}_transactors"
  max_size             = var.instance_count
  min_size             = var.instance_count
  launch_configuration = aws_launch_configuration.transactor.name
  vpc_zone_identifier  = var.subnets

  tag {
    key                 = "Name"
    value               = "${local.system_name}-transactor"
    propagate_at_launch = true
  }

  tag {
    key                 = "Type"
    value               = "Datomic"
    propagate_at_launch = true
  }
}
