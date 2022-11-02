(ns api.core
  (:require
   ["express" :as express]
   ["serverless-http" :as serverless]
   ["cors" :as cors]
   ["http" :as http]
   [promesa.core :as p]
   [api.config :as config]
   [api.lib.apollo :refer (start-server create-middleware)]))

(def dev? (= config/STAGE "dev"))

(defn create-app
  []
  (p/let [server (start-server)
          middleware (create-middleware server)
          app (express)]
    (.use app (cors #js{:origin "*"}) (.json express))
    (.use app (fn [_ res next]
                (doto res
                  (.set "Access-Control-Allow-Origin" "*")
                  (.set "Access-Control-Allow-Methods" "GET, POST")
                  (.set "Access-Control-Allow-Headers" "content-type"))
                (next)))
    (.use app middleware)
    app))

(defn start-server []
  (p/let [port 3000
          app (create-app)
          ^js server (.createServer http app)]
    (.listen server port (fn []
                           (println "listening on 3000...")))))

(when dev?
  (start-server))

(defn handler [event context]
  (p/let [app (create-app)
          handler (serverless app)
          result (handler event context)]
    result))
