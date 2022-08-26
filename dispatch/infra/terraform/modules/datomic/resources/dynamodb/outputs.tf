output "table_id" {
  value = aws_dynamodb_table.datomic.id
}

output "table_name" {
  value = aws_dynamodb_table.datomic.name
}

output "table_arn" {
  value = aws_dynamodb_table.datomic.arn
}
