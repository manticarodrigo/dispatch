(ns handlers.sync
  (:require
   [promesa.core :as p]
   [cljs-bean.core :refer (->js)]
   [model.user]
   [util.sequelize :refer (sequelize)]))

(defn sync
  [_ res]
  (p/let [_ (.query sequelize "CREATE EXTENSION IF NOT EXISTS postgis;")
          _ (.sync sequelize (->js {:alter true}))]
    (-> res (.send "Sync succeeded!"))))
