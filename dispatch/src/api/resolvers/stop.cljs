(ns api.resolvers.stop
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.stop :as model]))

(defn create-stop-arrival
  [_ args context _]
  (-> (model/create-stop-arrival context (->clj args))
      (p/catch anom/handle-resolver-error)))

(defn fetch-stop
  [_ args context _]
  (-> (model/find-unique context (->clj args))
      (p/catch anom/handle-resolver-error)))
