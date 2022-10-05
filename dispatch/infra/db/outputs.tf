output "cluster_endpoint" {
  value = module.db.cluster_endpoint
}

output "cluster_database_name" {
  value = module.db.cluster_database_name
}

output "cluster_port" {
  value = module.db.cluster_port
}

output "cluster_master_password" {
  value     = module.db.cluster_master_password
  sensitive = true
}

output "cluster_master_username" {
  value     = module.db.cluster_master_username
  sensitive = true
}

output "cluster_security_group_id" {
  value = module.db.security_group_id
}
