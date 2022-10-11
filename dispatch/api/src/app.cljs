(ns app
  (:require
   ["express$default" :as express]
   ["serverless-http$default" :as serverless]
   [promesa.core :as p]
   [config]
   [deps]
   [model.user]
   [util.sequelize :refer (open-sequelize close-sequelize)]
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
    (.post app "/register" register)
    (.post app "/login" login)
    app))

(def express-app (create-app))
(def serverless-app (serverless express-app))

(defn boostrap-models [sequelize]
  (model.user/init sequelize))

#js {:handler (fn [event context]
                (p/let [sequelize (open-sequelize boostrap-models)
                        res (serverless-app event context)
                        _ (close-sequelize sequelize)]
                  res))}
