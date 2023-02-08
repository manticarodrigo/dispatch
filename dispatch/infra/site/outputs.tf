output "rum_monitor_id" {
  value = aws_rum_app_monitor.site.app_monitor_id
}

output "rum_identity_pool_id" {
  value = aws_cognito_identity_pool.rum.id
}

output "rum_guest_role_arn" {
  value = aws_iam_role.rum.arn
}

output "site_bucket_name" {
  value = aws_s3_bucket.site_bucket.bucket
}
