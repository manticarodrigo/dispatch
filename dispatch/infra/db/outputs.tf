output "host" {
  value = aws_db_proxy.db_proxy.endpoint
}

output "name" {
  value = aws_db_instance.master.db_name
}

output "port" {
  value = aws_db_instance.master.port
}

output "password" {
  value     = aws_db_instance.master.password
  sensitive = true
}

output "username" {
  value     = aws_db_instance.master.username
  sensitive = true
}

output "lambda_security_group_id" {
  value = aws_security_group.lambda.id
}
