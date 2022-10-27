output "api_output" {
  value = archive_file.api.output_base64sha256
}
output "site_output" {
  value = archive_file.site.output_base64sha256
}
