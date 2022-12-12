(ns api.core
  (:require
   ["express" :as express]
   ["serverless-http" :as serverless]
   ["http" :as http]
   [promesa.core :as p]
   [cljs-bean.core :refer (->clj ->js)]
   [api.config :as config]
   [api.lib.apollo :as apollo]))

(def dev? (= config/STAGE "dev"))

(def headers {"Access-Control-Allow-Origin" "*"
              "Access-Control-Allow-Methods" "OPTIONS,POST,GET"
              "Access-Control-Allow-Headers" "Content-Type,Authorization"})
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
          app (create-app)
          ^js server (.createServer http app)]
    (.listen server port (fn []
                           (println "listening on 3000...")))))

(when dev?
  (start-server))

(defn handler [event context]
  (if (some-> event ->clj :httpMethod (= "OPTIONS"))
    (p/do
      (->js {:statusCode 204
             :headers headers}))
    (p/let [app (create-app)
            handler (serverless app)
            result (handler event context)]
      result)))
