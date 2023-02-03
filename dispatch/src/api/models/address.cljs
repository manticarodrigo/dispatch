(ns api.models.address
  (:require [promesa.core :as p]
            [goog.object :as gobj]
            [api.util.prisma :as prisma]
            [api.models.user :refer (logged-in-user)]))

(defn create [^js context {:keys [name description phone email lat lng]}]
  (p/let [^js user (logged-in-user context)]
    (prisma/create!
     (.. context -prisma -address)
     {:data {:name name
             :description description
             :phone phone
             :email email
             :lat lat
             :lng lng
             :user {:connect {:id (.-id user)}}}})))

(defn find-all [^js context]
  (p/-> (logged-in-user context {:include {:addresses {:orderBy {:name "asc"}}}})
        (gobj/get "addresses")))

(defn find-unique [^js context {:keys [id]}]
  (p/-> (logged-in-user context {:include {:addresses {:where {:id id}}}})
        (gobj/get "addresses")
        first))
