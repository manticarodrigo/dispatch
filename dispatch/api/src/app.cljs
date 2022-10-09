(ns app
  (:require
   ["express$default" :as express]
   ["serverless-http$default" :as serverless]
   ["http" :as http]
   [promesa.core :as p]
   [config]
   [deps]
   [model.user]
   [util.sequelize :refer (open-sequelize close-sequelize)]
   [handlers.auth :refer (register login)]))

(def dev? (= config/STAGE "dev"))

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

(defn boostrap-models [sequelize]
  (model.user/init sequelize))

(when dev?
  (open-sequelize boostrap-models)
  (start-server express-app))

#js {:handler (fn [event context]
                (p/let [_ (open-sequelize boostrap-models)
                        _ (serverless-app event context)
                        _ (close-sequelize)]))}
