output "api_invoke_url" {
  value = aws_apigatewayv2_stage.api.invoke_url
}

output "api_stage_name" {
  value = local.api_stage_name
}
