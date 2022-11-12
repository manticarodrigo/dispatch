(ns api.resolvers.seat
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.seat :as models.seat]))

(defn create-seat
  [_ args context _]
  (-> (p/let [payload (->clj args)
              seat-id (models.seat/create context payload)]
        seat-id)
      (p/catch anom/handle-resolver-error)))
