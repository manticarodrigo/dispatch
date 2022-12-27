(ns api.core
  (:require
   ["express" :as express]
   ["@as-integrations/aws-lambda" :refer (startServerAndCreateLambdaHandler)]
   ["http" :as http]
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [api.config :as config]
   [api.lib.apollo :as apollo]))

(def dev? (= config/STAGE "dev"))

(def headers {"Access-Control-Allow-Origin" "*"
              "Access-Control-Allow-Methods" "OPTIONS,POST,GET"
              "Access-Control-Allow-Headers" "authorization,content-type,x-datadog-origin,x-datadog-parent-id,x-datadog-sampling-priority,x-datadog-trace-id"
              "Access-Control-Max-Age" 86400
              "Cache-Control" "public, max-age=86400"
              "Vary" "origin"})

(defn create-app
  []
  (p/let [server (apollo/start-server)
          middleware (apollo/create-middleware server)
          app (express)]
    (.use app (.json express))
    (.use app (fn [req res next]
                (.set res (->js headers))
                (if (= (.. req -method) "OPTIONS")
                  (.send res 200)
                  (next))))
    (.use app middleware)
    app))

(defn start-server []
  (p/let [port 3000
          ^js app (create-app)
          ^js server (.createServer http app)]
    (.listen server port (fn []
                           (println "listening on 3000...")))))

(when dev?
  (start-server))

(def handler
  (when-not dev?
    (startServerAndCreateLambdaHandler apollo/server)))
