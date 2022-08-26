module "vpc" {
  source = "./modules/vpc"
}

module "datomic" {
  source          = "./modules/datomic"
  env             = var.env
  aws_account_id  = var.aws_account_id
  aws_region      = var.aws_region
  datomic_ami     = var.datomic_ami
  datomic_license = var.datomic_license
  subnets         = module.vpc.database_subnets
  vpc_id          = module.vpc.vpc_id
  vpc_ip_block    = module.vpc.vpc_cidr_block
}
