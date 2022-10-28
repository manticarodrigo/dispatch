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
    working_dir = "../dispatch"
  }
}

data "archive_file" "api" {
  type        = "zip"
  source_dir  = "${path.module}/../../dispatch/out"
  output_path = "${path.module}/api.zip"
  depends_on  = [null_resource.build]
}
