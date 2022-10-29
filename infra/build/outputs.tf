output "site_build_id" {
  value = null_resource.build_site.id
}

output "api_output" {
  value = data.archive_file.api.output_base64sha256
}
