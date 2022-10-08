(ns app
  (:require
   ["express$default" :as express]
   ["serverless-http$default" :as serverless]
   ["http" :as http]
   [config]
   [deps]
   [handlers.sync :refer (sync)]
   [handlers.auth :refer (register login)]))

(def dev? (= config/APP_ENV "dev"))

(defn create-app
  []
  (let [app (express)]
    (.use app (.json express))
    (.use app (fn [_ res next]
                (doto res
                  (.set "Access-Control-Allow-Origin" "*")
                  (.set "Access-Control-Allow-Methods" "GET, POST")
                  (.set "Access-Control-Allow-Headers" "content-type"))
                (next)))
    (.get app "/sync" sync)
    (.post app "/register" register)
    (.post app "/login" login)
    app))

(defn start-server
  [app & {:keys [port callback]
          :or {port 3000
               callback (fn [] (.log js/console "Listening on port 3000!"))}}]
  (let [server (.createServer http app)]
    (.listen server port callback)))

(def express-app (create-app))
(def serverless-app (serverless express-app))

(when dev? (start-server express-app))

#js {:handler serverless-app}
