(ns express
  (:require ["express$default" :as express]
            ["http" :as http]
            [handlers.sync :refer (sync)]
            [handlers.auth :refer (register login)]))

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

(defn stop-server
  [server]
  (.close server))
