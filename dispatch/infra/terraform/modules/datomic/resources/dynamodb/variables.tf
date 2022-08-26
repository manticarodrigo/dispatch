variable "dynamo_read_capacity" {
  description = "One read capacity unit represents one strongly consistent read per second, or two eventually consistent reads per second, for items up to 4 KB in size."
  default     = 50
}

variable "dynamo_write_capacity" {
  description = "One write capacity unit represents one write per second for items up to 1 KB in size."
  default     = 50
}
