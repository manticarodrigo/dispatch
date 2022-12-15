(ns api.models.stop
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create-stop-arrival [^js context {:keys [stopId]}]
  (p/let [^js stop (prisma/update!
                    (.. context -prisma -stop)
                    {:where {:id stopId}
                     :data {:arrivedAt (js/Date.)}})]
    ^js stop))
