(ns api.core
  (:require
   ["express" :as express]
   ["serverless-http" :as serverless]
   ["http" :as http]
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [api.config :as config]
   [api.lib.apollo :as apollo]))

(def dev? (= config/STAGE "local"))

(def headers {"Access-Control-Allow-Origin" "*"
              "Access-Control-Allow-Methods" "OPTIONS,POST,GET"
              "Access-Control-Allow-Headers" "authorization,content-type,x-amzn-trace-id"
              "Access-Control-Max-Age" 86400
              "Cache-Control" "public, max-age=86400"
              "Vary" "origin"})

(defn create-app
  []
  (let [server (apollo/start-server)
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

(def app (create-app))

(defn start-server []
  (p/let [port 3000
          ^js server (.createServer http app)]
    (.listen server port (fn []
                           (println "listening on 3000...")))))

(when dev?
  (start-server))

(defn handler [event context]
  (p/let [handler (serverless app)
          result (handler event context)]
    result))
