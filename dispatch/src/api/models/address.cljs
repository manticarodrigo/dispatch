(ns api.models.address
  (:require
   [promesa.core :as p]
   [api.util.prisma :as prisma]))

(defn create [^js context {:keys [name description phone email lat lng]}]
  (p/let [user-id (.. context -user -id)
          ^js address (prisma/create!
                       (.. context -prisma -address)
                       {:data {:name name
                               :description description
                               :phone phone
                               :email email
                               :lat lat
                               :lng lng
                               :user {:connect {:id user-id}}}})]
    (some-> address .-id)))

(defn find-all [^js context]
  (prisma/find-many
   (.. context -prisma -address)
   {:where {:user {:id (.. context -user -id)}}
    :orderBy {:name "asc"}}))

(defn find-unique [^js context {:keys [id]}]
  (prisma/find-unique
   (.. context -prisma -address)
   {:where {:id id}}))
