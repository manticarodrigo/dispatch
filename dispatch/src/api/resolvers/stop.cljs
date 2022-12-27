(ns api.resolvers.stop
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.stop :as model]))

(defn create-stop-arrival
  [_ args context _]
  (model/create-stop-arrival context (->clj args)))

(defn fetch-stop
  [_ args context _]
  (model/find-unique context (->clj args)))
