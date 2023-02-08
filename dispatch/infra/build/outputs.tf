
output "sha1" {
  value = local.sha1
}

output "build" {
  value = null_resource.build
}
