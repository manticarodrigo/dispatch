(ns api.resolvers.address
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.address :as model]))

(defn create-address
  [_ args context _]
  (model/create context (->clj args)))

(defn fetch-addresses
  [_ _ context _]
  (model/find-all context))

(defn fetch-address
  [_ args context _]
  (model/find-unique context (->clj args)))
