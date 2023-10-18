output "vpc_id" {
  value = data.aws_vpc.default.id
}

output "default_subnet_ids" {
  value = data.aws_db_subnet_group.default.subnet_ids
}
