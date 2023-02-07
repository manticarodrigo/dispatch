(ns api.models.place
  (:require [promesa.core :as p]
            [goog.object :as gobj]
            [api.util.prisma :as prisma]
            [api.models.user :refer (active-user)]))

(defn create [^js context {:keys [name phone email description lat lng]}]
  (p/let [^js user (active-user context)]
    (prisma/create!
     (.. context -prisma -place)
     {:data {:name name
             :phone phone
             :email email
             :description description
             :lat lat
             :lng lng
             :user {:connect {:id (.-id user)}}}})))

(defn find-all [^js context]
  (p/-> (active-user context {:include {:places {:orderBy {:name "asc"}}}})
        (gobj/get "places")))

(defn find-unique [^js context {:keys [placeId]}]
  (p/-> (active-user context {:include {:places {:where {:id placeId}}}})
        (gobj/get "places")
        first))
