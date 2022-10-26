(ns app
  (:require
   ["express$default" :as express]
   ["serverless-http$default" :as serverless]
   ["cors$default" :as cors]
   ["http" :as http]
   [promesa.core :as p]
   [config]
   [lib.apollo :refer (create-server create-middleware)]
   [tests :refer (run-tests)]))

(def dev? (= config/STAGE "dev"))

(defn create-app
  []
  (p/let [server (create-server)
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
          server (.createServer http app)]
    (.listen server port (fn []
                           (js/console.log "Listening on port 3000!")
                           (run-tests)))))

(when dev?
  (start-server))

#js {:handler
     (fn [event context]
       (p/let [app (create-app)
               handler (serverless app)
               result (handler event context)]
         result))}
