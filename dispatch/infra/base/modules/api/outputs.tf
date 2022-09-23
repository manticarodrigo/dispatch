output "function_name" {
  value = aws_lambda_function.api.function_name
}

output "base_url" {
  value = aws_apigatewayv2_stage.lambda.invoke_url
}
