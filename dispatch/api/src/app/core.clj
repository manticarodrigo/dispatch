(ns app.core
  (:require [datomic.client.api :as d]
            [app.schema :refer (schema-tx)]
            [app.seed :refer (seed-tx)]))

(def client (d/client {:server-type :dev-local
                       :storage-dir :mem
                       :system "dev"}))

(defn- connect []
  (let [opts {:db-name "app"}]
    (d/delete-database client opts)
    (d/create-database client opts)
    (d/connect client opts)))

(defn -main []
  (println "app started")
  (let [conn (connect)
        schema-res (d/transact conn {:tx-data schema-tx})
        seed-res (d/transact conn {:tx-data seed-tx})
        route-id (-> seed-res :tempids (get "temp-route"))
        db (d/db conn)]
    (println "route id: " route-id)
    (println "route list: " (d/q
                             '[:find ?r
                               :in $
                               :where
                               [?r :route/driver _]
                               [?r :route/orders _]
                               [?r :route/origin _]] db))))
