resource "null_resource" "build" {
  triggers = {
    updated_at = timestamp()
  }

  provisioner "local-exec" {
    command     = <<-EOT
                  yarn
                  yarn release
                  yarn install --production
                  cp -r node_modules out/node_modules
                  EOT
    working_dir = "${path.module}/../../dispatch"
  }
}

data "archive_file" "api" {
  type        = "zip"
  source_dir  = "${path.module}/../../dispatch/out"
  output_path = "${path.module}/api.zip"
  depends_on  = [null_resource.build]
}

data "archive_file" "site" {
  type        = "zip"
  source_dir  = "${path.module}/../../dispatch/public"
  output_path = "${path.module}/site.zip"
  depends_on  = [null_resource.build]
}
