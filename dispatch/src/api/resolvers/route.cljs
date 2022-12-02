(ns api.resolvers.route
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.route :as model]))

(defn create-route
  [_ args context _]
  (-> (p/let [payload (->clj args)
              seat-id (model/create context payload)]
        seat-id)
      (p/catch anom/handle-resolver-error)))

(defn fetch-routes
  [_ _ context _]
  (-> (model/find-all context)
      (p/catch anom/handle-resolver-error)))

(defn fetch-route
  [_ args context _]
  (-> (model/find-unique context (->clj args))
      (p/catch anom/handle-resolver-error)))
