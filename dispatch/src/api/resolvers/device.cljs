(ns api.resolvers.device
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.device :as device]))

(defn create
  [_ args context _]
  (device/create context (->clj args)))
