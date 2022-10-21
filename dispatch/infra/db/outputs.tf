output "host" {
  value = aws_db_instance.master.address
}

output "name" {
  value = aws_db_instance.master.db_name
}

output "port" {
  value = aws_db_instance.master.port
}

output "username" {
  value     = aws_db_instance.master.username
  sensitive = true
}

output "password" {
  value     = aws_db_instance.master.password
  sensitive = true
}
