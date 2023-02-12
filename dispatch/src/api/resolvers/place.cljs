(ns api.resolvers.place
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.place :as place]))

(defn create
  [_ args context _]
  (place/create context (->clj args)))

(defn find-all
  [_ args context _]
  (place/find-all context (->clj args)))

(defn find-unique
  [_ args context _]
  (place/find-unique context (->clj args)))
