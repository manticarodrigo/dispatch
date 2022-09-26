output "cluster_endpoint" {
  description = "Writer endpoint for the cluster"
  value       = aws_rds_cluster.db.endpoint
}

output "cluster_database_name" {
  description = "Name for an automatically created database on cluster creation"
  value       = aws_rds_cluster.db.database_name
}

output "cluster_port" {
  description = "The database port"
  value       = aws_rds_cluster.db.port
}

output "cluster_master_password" {
  description = "The database master password"
  value       = aws_rds_cluster.db.master_password
  sensitive   = true
}

output "cluster_master_username" {
  description = "The database master username"
  value       = aws_rds_cluster.db.master_username
  sensitive   = true
}
