(ns api.core
  (:require
   ["express" :as express]
   ["serverless-http" :as serverless]
   ["http" :as http]
   [promesa.core :as p]
   [api.config :as config]
   [api.lib.apollo :as apollo]))

(def dev? (= config/STAGE "dev"))

(defn create-app
  []
  (p/let [server (apollo/start-server)
          middleware (apollo/create-middleware server)
          app (express)]
    (.use app (.json express))
    (.use app (fn [_ res next]
                (doto res
                  (.set "Access-Control-Allow-Origin" "*")
                  (.set "Access-Control-Allow-Methods" "POST,GET")
                  (.set "Access-Control-Allow-Headers" "content-type,authorization"))
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
  (js/console.log "event" event)
  (js/console.log "method" (some-> event .-httpMethod))
  (js/console.log "options" (some-> event .-httpMethod (= "OPTIONS")))
  (if (some-> event .-httpMethod (= "OPTIONS"))
    #js{:statusCode 204
        :headers
        {:Access-Control-Allow-Headers "Content-Type"
         :Access-Control-Allow-Origin "*"
         :Access-Control-Allow-Methods "OPTIONS,POST,GET"}}
    (p/let [app (create-app)
            handler (serverless app)
            result (handler event context)]
      result)))
