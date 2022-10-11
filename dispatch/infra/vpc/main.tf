data "aws_vpc" "default" {
  default = true
}

data "aws_db_subnet_group" "default" {
  name = "default"
}
