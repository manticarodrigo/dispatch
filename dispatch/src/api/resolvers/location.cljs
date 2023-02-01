(ns api.resolvers.location
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.location :as location]))

(defn create-location
  [_ args context _]
  (location/create context (->clj args)))
