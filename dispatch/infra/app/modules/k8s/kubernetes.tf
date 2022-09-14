locals {
  app_name  = "dispatch"
  app_label = "DispatchTransactor"
}

data "aws_eks_cluster" "cluster" {
  name = var.cluster_id
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
  exec {
    api_version = "client.authentication.k8s.io/v1alpha1"
    command     = "aws"
    args = [
      "eks",
      "get-token",
      "--cluster-name",
      data.aws_eks_cluster.cluster.name
    ]
  }
}

resource "kubernetes_deployment" "transactor" {
  metadata {
    name = "${local.app_name}-transactor"
    labels = {
      App = local.app_label
    }
  }

  spec {
    replicas = 2
    selector {
      match_labels = {
        App = local.app_label
      }
    }
    template {
      metadata {
        labels = {
          App = local.app_label
        }
      }
      spec {
        container {
          image = "nginx:1.7.8"
          name  = "example"

          port {
            container_port = 80
          }

          resources {
            limits = {
              cpu    = "0.5"
              memory = "512Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "50Mi"
            }
          }
        }
      }
    }
  }
}
