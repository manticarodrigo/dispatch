(ns api.resolvers.location
  (:require [promesa.core :as p]
            [cljs-bean.core :refer (->clj)]
            [api.util.anom :as anom]
            [api.models.location :as models.location]))

(defn create-location
  [_ args context _]
  (-> (models.location/create context (->clj args))
      (p/catch anom/handle-resolver-error)))
