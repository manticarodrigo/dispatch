(ns api.models.waypoint
  (:require [api.util.prisma :as prisma]))

(defn create-arrival [^js context {:keys [waypointId note]}]
  (prisma/update!
   (.. context -prisma -waypoint)
   {:where {:id waypointId}
    :data {:arrivedAt (js/Date.) :note note}}))

(defn find-unique [^js context {:keys [waypointId]}]
  (prisma/find-unique
   (.. context -prisma -waypoint)
   {:where {:id waypointId}
    :include {:place true}}))
