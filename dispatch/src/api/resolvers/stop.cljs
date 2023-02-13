(ns api.resolvers.stop
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.stop :as stop]))

(defn create-arrival
  [_ args context _]
  (stop/create-arrival context (->clj args)))

(defn find-unique
  [_ args context _]
  (stop/find-unique context (->clj args)))
