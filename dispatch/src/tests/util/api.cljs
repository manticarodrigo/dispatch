(ns tests.util.api
  (:require [promesa.core :as p]
            [cljs-bean.core :refer (bean ->js)]
            [api.lib.apollo :refer (create-server options)]))

(defonce !server (atom nil))

(defn init-server []
  (p/let [server (if @!server @!server (create-server))]
    (reset! !server server)
    server))

(defn send [body]
  (p/let [^js server (init-server)
          context (.context options)
          ^js res (.executeOperation server (->js body) (->js {:contextValue context}))
          body (bean (.. res -body -singleResult) :recursive true)]
    {:data (some-> body :data (bean :recursive true))
     :errors (some-> body :errors)}))
