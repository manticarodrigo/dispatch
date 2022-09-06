module "ecr" {
  source = "terraform-aws-modules/ecr/aws"

  repository_name = var.app_name

  repository_read_write_access_arns = ["arn:aws:iam::${aws_account_id}:user/dispatch-github-actions"]

  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1,
        description  = "Keep last 30 images",
        selection = {
          tagStatus     = "tagged",
          tagPrefixList = ["v"],
          countType     = "imageCountMoreThan",
          countNumber   = 30
        },
        action = {
          type = "expire"
        }
      }
    ]
  })
}
