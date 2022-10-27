output "api_output" {
  value = data.archive_file.api.output_base64sha256
}
output "site_output" {
  value = data.archive_file.site.output_base64sha256
}
