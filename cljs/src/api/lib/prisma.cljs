(ns api.lib.prisma
  (:require ["@prisma/client" :refer (PrismaClient)]
            [api.config :as config]))

(def prisma (PrismaClient.
             (when (and (not= config/STAGE "test")
                        (not= config/STAGE "local"))
               #js{:log #js["query" "info" "warn" "error"]})))
