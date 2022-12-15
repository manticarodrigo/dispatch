output "api_invoke_url" {
  value = aws_apigatewayv2_stage.api.invoke_url
}

output "api_stage_name" {
  value = local.api_stage_name
}

output "api_lambda_arn" {
  value = aws_lambda_function.api.arn
}
