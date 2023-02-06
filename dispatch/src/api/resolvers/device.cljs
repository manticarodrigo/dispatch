(ns api.resolvers.device
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.device :as device]))

(defn link-device
  [_ args context _]
  (device/create context (->clj args)))