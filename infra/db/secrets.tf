resource "aws_secretsmanager_secret" "rds_secret" {
  name_prefix             = "${var.app_name}-rds-proxy-secret-${terraform.workspace}"
  recovery_window_in_days = 7
}

resource "aws_secretsmanager_secret_version" "rds_secret_version" {
  secret_id = aws_secretsmanager_secret.rds_secret.id
  secret_string = jsonencode({
    "username"             = aws_db_instance.master.username
    "password"             = aws_db_instance.master.username
    "engine"               = aws_db_instance.master.engine
    "host"                 = aws_db_instance.master.address
    "port"                 = aws_db_instance.master.port
    "dbInstanceIdentifier" = aws_db_instance.master.id
  })
}
