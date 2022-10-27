output "id" {
  value = null_resource.build.id
}

output "api_output" {
  value = data.archive_file.api.output_base64sha256
}
