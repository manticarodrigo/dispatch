(ns api.lib.prisma
  (:require ["@prisma/client" :refer (PrismaClient)]))

(def !prisma (atom nil))

(defn open-prisma []
  (let [prisma (or @!prisma (PrismaClient.))]
    (reset! !prisma prisma)
    prisma))
