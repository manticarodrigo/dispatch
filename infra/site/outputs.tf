output "distribution_id" {
  value = aws_cloudfront_distribution.site_s3_dist.id
}

output "bucket_name" {
  value = aws_s3_bucket.site_bucket.id
}
