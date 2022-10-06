output "endpoint" {
  value = aws_db_instance.db_master.endpoint
}

output "name" {
  value = aws_db_instance.db_master.db_name
}

output "port" {
  value = aws_db_instance.db_master.port
}

output "password" {
  value     = aws_db_instance.db_master.password
  sensitive = true
}

output "username" {
  value     = aws_db_instance.db_master.username
  sensitive = true
}
