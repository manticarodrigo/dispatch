(ns app.schema)

(def location
  [{:db/ident :location/lat
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one
    :db/doc "Location latitude"}

   {:db/ident :location/lng
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one
    :db/doc "Location longitude"}])

(def item
  [{:db/ident :item/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Order item name"}

   {:db/ident :item/image
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Order item image"}])

(def receipt
  [{:db/ident :receipt/signature
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Order receipt signature"}

   {:db/ident :receipt/image
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Order receipt image"}

   {:db/ident :receipt/codes
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc "Order receipt codes"}])

(def user
  [{:db/ident :user/username
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/value
    :db/doc "User username"}

   {:db/ident :user/firstname
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "User first name"}

   {:db/ident :user/lastname
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "User last name"}

   {:db/ident :user/image
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "User image"}

   {:db/ident :user/location
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/isComponent true
    :db/doc "User location"}])

(def order
  [{:db/ident :order/items
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/isComponent true
    :db/doc "Order items"}

   {:db/ident :order/customer
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Order customer"}

   {:db/ident :order/receipt
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Order receipt"}])

(def route
  [{:db/ident :route/driver
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Route driver"}

   {:db/ident :route/orders
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "Route orders"}

   {:db/ident :route/origin
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/isComponent true
    :db/doc "Route origin"}

   {:db/ident :route/destination
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/isComponent true
    :db/doc "Route destination"}])

(def schema-tx
  (into []
        (concat location
                item
                receipt
                user
                order
                route)))
