(ns api.resolvers.address
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.address :as models.address]))

(defn create-address
  [_ args context _]
  (-> (p/let [payload (->clj args)
              seat-id (models.address/create context payload)]
        seat-id)
      (p/catch anom/handle-resolver-error)))

(defn fetch-addresses
  [_ _ context _]
  (-> (models.address/find-all context)
      (p/catch anom/handle-resolver-error)))
