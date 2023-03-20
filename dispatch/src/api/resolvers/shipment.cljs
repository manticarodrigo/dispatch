(ns api.resolvers.shipment
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.shipment :as shipment]))

(defn create-shipment
  [_ args context _]
  (shipment/create-shipment context (->clj args)))

(defn create-shipments
  [_ args context _]
  (shipment/create-shipments context (->clj args)))

(defn archive-shipments
  [_ args context _]
  (shipment/archive-shipments context (->clj args)))

(defn fetch-organization-shipments
  [_ _ context _]
  (shipment/fetch-organization-shipments context))
