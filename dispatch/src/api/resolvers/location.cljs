(ns api.resolvers.location
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.location :as model]))

(defn create-location
  [_ args context _]
  (model/create context (->clj args)))
