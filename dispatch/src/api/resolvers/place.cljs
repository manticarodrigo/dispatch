(ns api.resolvers.place
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.place :as place]))

(defn create-place
  [_ args context _]
  (place/create-place context (->clj args)))

(defn fetch-organization-places
  [_ _ context _]
  (place/fetch-organization-places context))

(defn fetch-organization-place
  [_ args context _]
  (place/fetch-organization-place context (->clj args)))

(defn fetch-agent-places
  [^js parent _ context _]
  (or (.. parent -places)
      (place/fetch-agent-places context)))

(defn fetch-agent-place
  [_ args context _]
  (place/fetch-agent-place context (->clj args)))
