(ns app.core
  (:require [datomic.api :as d]
            [app.schema :refer (schema)]
            [app.seed :refer (seed-tx)]))

(def db-uri "datomic:mem://dispatch")

(defn- connect [opts]
  (d/delete-database opts)
  (d/create-database opts)
  (d/connect opts))

(defn- seed [conn]
  (d/transact conn schema)
  (d/transact conn seed-tx))

(defn -main []
  (let [conn (connect db-uri)
        db #(d/db conn)
        res (:tempids @(seed conn))]
    (println res)
    (println "route list: " (d/q
                             '[:find [?r ...]
                               :in $
                               :where
                               [?r :route/driver _]
                               [?r :route/orders _]
                               [?r :route/origin _]]
                             (db)))
    (println "route: " (d/pull (db) '[*] (-> res (get "temp-route"))))))
