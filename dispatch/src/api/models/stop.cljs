(ns api.models.stop
  (:require [api.util.prisma :as prisma]))

(defn create-arrival [^js context {:keys [stopId note]}]
  (prisma/update!
   (.. context -prisma -stop)
   {:where {:id stopId}
    :data {:arrivedAt (js/Date.) :note note}}))

(defn find-unique [^js context {:keys [stopId]}]
  (prisma/find-unique
   (.. context -prisma -stop)
   {:where {:id stopId}
    :include {:place true}}))
