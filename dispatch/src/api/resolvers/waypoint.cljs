(ns api.resolvers.waypoint
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.waypoint :as models.waypoint]))

(defn create-waypoint
  [_ args context _]
  (-> (p/let [payload (->clj args)
              seat-id (models.waypoint/create context payload)]
        seat-id)
      (p/catch anom/handle-resolver-error)))

(defn find-waypoints
  [_ _ context _]
  (-> (models.waypoint/find-all context)
      (p/catch anom/handle-resolver-error)))
