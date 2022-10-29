locals {
  common_dirs       = ["../dispatch/config", "../dispatch/resources"]
  common_files      = ["../dispatch/package.json", "../dispatch/shadow-cljs.edn"]
  common_dirs_sha1  = join("", [for d in local.common_dirs : join("", [for f in fileset(d, "**") : filesha1("${d}/${f}")])])
  common_files_sha1 = join("", [for f in local.common_files : filesha1(f)])
  common_sha1       = join("", [local.common_dirs_sha1, local.common_files_sha1])

  api_dirs      = ["../dispatch/src/api"]
  api_dirs_sha1 = join("", [for d in local.api_dirs : join("", [for f in fileset(d, "**") : filesha1("${d}/${f}")])])
  api_sha1      = join("", [local.api_dirs_sha1])

  site_dirs       = ["../dispatch/src/ui", "../dispatch/public/fonts", "../dispatch/public/images"]
  site_files      = ["../dispatch/public/index.src.html"]
  site_dirs_sha1  = join("", [for d in local.site_dirs : join("", [for f in fileset(d, "**") : filesha1("${d}/${f}")])])
  site_files_sha1 = join("", [for f in local.site_files : filesha1(f)])
  site_sha1       = join("", [local.site_dirs_sha1])
}
resource "null_resource" "build_api" {
  triggers = {
    api_sha1 = local.api_sha1
  }

  provisioner "local-exec" {
    command     = <<-EOT
                  yarn install --production
                  cp -r node_modules out/node_modules
                  yarn
                  yarn release:api
                  EOT
    working_dir = "../dispatch"
  }
}

resource "null_resource" "build_site" {
  triggers = {
    site_sha1 = local.site_sha1
  }

  provisioner "local-exec" {
    command     = <<-EOT
                  yarn
                  yarn release:ui
                  EOT
    working_dir = "../dispatch"
  }
}

data "archive_file" "api" {
  type        = "zip"
  source_dir  = "../dispatch/out"
  output_path = "../dispatch/out/api.zip"
  depends_on  = [null_resource.build_api]
}
