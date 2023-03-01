(ns api.core
  (:require
   ["express" :as express]
   ["serverless-http" :as serverless]
   ["http" :as http]
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [api.config :as config]
   [api.lib.apollo :as apollo]
   [api.lib.google.optimization]))

(def dev? (= config/STAGE "local"))

(def headers {"Access-Control-Allow-Origin" "*"
              "Access-Control-Allow-Methods" "OPTIONS,POST,GET"
              "Access-Control-Allow-Headers" "authorization,content-type,x-datadog-origin,x-datadog-parent-id,x-datadog-sampling-priority,x-datadog-trace-id"
              "Access-Control-Max-Age" 86400
              "Cache-Control" "public, max-age=86400"
              "Vary" "origin"})

;; TODO: move to express lib
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
    (.post app "/update"
           (fn [^js req ^js res]
             (let [version-name (.. req -body -version_name)
                   version-build (.. req -body -version_build)
                   version (if (or (= version-name "builtin") (not version-name))
                             version-build
                             version-name)]
               (js/console.log "APP VERSION: " config/VERSION)
               (js/console.log "MOBILE UPDATE REQUEST: " (.-body req))
               (.send res (if (and version (not= version config/VERSION))
                            #js{:version config/VERSION
                                :url (str
                                      "https://s3.amazonaws.com/"
                                      config/SITE_BUCKET_NAME
                                      "/app.zip")}
                            #js{:message "No update available"
                                :version ""
                                :url ""})))))
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
