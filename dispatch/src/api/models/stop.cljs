(ns api.models.stop
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create-stop-arrival [^js context {:keys [stopId note]}]
  (p/let [^js stop (prisma/update!
                    (.. context -prisma -stop)
                    {:where {:id stopId}
                     :data {:arrivedAt (js/Date.) :note note}})]
    ^js stop))

(defn find-unique [^js context {:keys [id]}]
  (prisma/find-unique
   (.. context -prisma -stop)
   {:where {:id id}
    :include {:address true}}))
