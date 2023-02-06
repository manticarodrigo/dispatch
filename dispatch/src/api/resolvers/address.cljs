(ns api.resolvers.address
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.address :as address]))

(defn create-address
  [_ args context _]
  (address/create context (->clj args)))

(defn fetch-addresses
  [_ _ context _]
  (address/find-all context))

(defn fetch-address
  [_ args context _]
  (address/find-unique context (->clj args)))
