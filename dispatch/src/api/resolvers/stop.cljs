(ns api.resolvers.stop
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj)]
   [api.util.anom :as anom]
   [api.models.stop :as models.stop]))

(defn create-arrived-at
  [_ args context _]
  (-> (models.stop/create-arrived-at context (->clj args))
      (p/catch anom/handle-resolver-error)))

