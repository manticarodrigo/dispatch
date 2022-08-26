module "dynamodb" {
  source = "./resources/dynamodb"
}

module "transactor" {
  source = "./resources/transactor"

  env             = var.env
  aws_account_id  = var.aws_account_id
  aws_region      = var.aws_region
  datomic_ami     = var.datomic_ami
  datomic_license = var.datomic_license
  subnets         = var.subnets
  vpc_id          = var.vpc_id
  vpc_ip_block    = var.vpc_ip_block

  dynamodb_table_id = module.dynamodb.table_id
}
