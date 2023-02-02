(ns api.lib.prisma
  (:require ["@prisma/client" :refer (PrismaClient)]
            [promesa.core :as p]
            [api.config :as config]))

(def prisma (PrismaClient.))

(when (not= config/STAGE "local")
  (.$use prisma
         (fn [^js params next]
           (p/let [before (js/Date.now)
                   result (next params)
                   after (js/Date.now)]
             (prn
              (str "Query "
                   (.-model params)
                   "."
                   (.-action params)
                   " took "
                   (- after before)
                   "ms"))
             result))))

