(ns lib.sequelize
  (:require ["sequelize" :refer (Sequelize)]
            [cljs-bean.core :refer (->js)]
            [promesa.core :as p]
            [config]))

(def !sequelize (atom nil))

(defn sync-sequelize [sequelize]
  (.sync sequelize #js{:alter true}))

(defn load-sequelize []
  (let [sequelize (Sequelize.
                   (->js {:host config/PGHOST
                          :database config/PGDATABASE
                          :port config/PGPORT
                          :username config/PGUSER
                          :password config/PGPASSWORD
                          :dialect "postgres"
                          :logging false
                          :pool {:max 2
                                 :min 0
                                 :idle 0
                                 :acquire 3000
                                 :evict 10000}}))]
    (-> (.authenticate sequelize)
        (.then (fn [] sequelize)))))

(defn reinit-sequelize [sequelize]
  (.initPools (.. sequelize -connectionManager))
  (when (.hasOwnProperty (.. sequelize -connectionManager) "getConnection")
    (js-delete (.. sequelize -connectionManager) "getConnection")))

(defn open-sequelize []
  (p/let [loaded (some? @!sequelize)
          sequelize (or @!sequelize (load-sequelize))
          _ (reset! !sequelize sequelize)
          _ (when loaded (reinit-sequelize sequelize))]
    sequelize))

(defn close-sequelize [sequelize]
  (.close (.. sequelize -connectionManager)))
