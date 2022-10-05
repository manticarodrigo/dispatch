output "cluster_endpoint" {
  value = aws_rds_cluster.cluster.endpoint
}

output "cluster_database_name" {
  value = aws_rds_cluster.cluster.database_name
}

output "cluster_port" {
  value = aws_rds_cluster.cluster.port
}

output "cluster_master_password" {
  value     = aws_rds_cluster.cluster.master_password
  sensitive = true
}

output "cluster_master_username" {
  value     = aws_rds_cluster.cluster.master_username
  sensitive = true
}
