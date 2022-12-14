locals {
  dirs = [
    "../dispatch/src",
    "../dispatch/config",
    "../dispatch/resources",
    "../dispatch/prisma",
    "../dispatch/public/fonts",
    "../dispatch/public/images"
  ]
  files = [
    "../dispatch/package.json",
    "../dispatch/shadow-cljs.edn",
    "../dispatch/public/index.src.html"
  ]
  dirs_sha1  = join("", [for d in local.dirs : join("", [for f in fileset(d, "**") : filesha1("${d}/${f}")])])
  files_sha1 = join("", [for f in local.files : filesha1(f)])
  sha1       = sha1(join("", [local.dirs_sha1, local.files_sha1]))
}

resource "null_resource" "build" {
  triggers = {
    sha1 = local.sha1
  }

  provisioner "local-exec" {
    interpreter = ["/bin/bash", "-c"]
    command     = <<-EOT
                  set -e
                  source local.env
                  yarn
                  yarn db-gen
                  yarn test
                  npx prisma generate --data-proxy
                  yarn release
                  EOT
    working_dir = "../dispatch"

    environment = {
      API_URL       = "https://api.${var.app_name}.${var.domain_name}"
      SECURE_COOKIE = true
    }
  }
}
