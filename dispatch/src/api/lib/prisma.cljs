(ns api.lib.prisma
  (:require ["@prisma/client" :refer (PrismaClient)]))

(def prisma (PrismaClient.))
