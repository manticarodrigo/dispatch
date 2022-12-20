(ns api.resolvers.stop
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.stop :as models.stop]))

(defn create-stop-arrival
  [_ args context _]
  (-> (models.stop/create-stop-arrival context (->clj args))
      ;;(p/catch anom/handle-resolver-error)
      ))

