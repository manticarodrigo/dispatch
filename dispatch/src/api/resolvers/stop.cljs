(ns api.resolvers.stop
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.stop :as stop]))

(defn create-stop-arrival
  [_ args context _]
  (stop/create-stop-arrival context (->clj args)))

(defn fetch-stop
  [_ args context _]
  (stop/find-unique context (->clj args)))
