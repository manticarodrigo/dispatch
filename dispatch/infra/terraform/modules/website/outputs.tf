output "distribution_id" {
  value = aws_cloudfront_distribution.s3_dist.id
}
output "domain_name" {
  value = aws_cloudfront_distribution.s3_dist.domain_name
}
output "zone_id" {
  value = aws_cloudfront_distribution.s3_dist.hosted_zone_id
}
output "bucket_name" {
  value = aws_s3_bucket.site_bucket.id
}
