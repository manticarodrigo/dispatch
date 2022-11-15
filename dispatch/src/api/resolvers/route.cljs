(ns api.resolvers.route
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.route :as models.route]))

(defn create-route
  [_ args context _]
  (-> (p/let [payload (->clj args)
              seat-id (models.route/create context payload)]
        seat-id)
      (p/catch anom/handle-resolver-error)))

(defn find-routes
  [_ _ context _]
  (-> (models.route/find-all context)
      (p/catch anom/handle-resolver-error)))
