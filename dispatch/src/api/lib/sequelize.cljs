(ns api.lib.sequelize
  (:require ["sequelize" :refer (Sequelize)]
            [cljs-bean.core :refer (->js)]
            [promesa.core :as p]
            [api.config :as config]))

(def !sequelize (atom nil))

(defn sync-sequelize [sequelize]
  (.sync sequelize #js{:alter true}))

(defn load-sequelize []
  (let [sequelize (Sequelize.
                   (->js {:host config/DB_HOST
                          :database config/DB_NAME
                          :port config/DB_PORT
                          :username config/DB_USER
                          :password config/DB_PASSWORD
                          :dialect "postgres"
                          :logging false
                          :pool {:max 2
                                 :min 0
                                 :idle 0
                                 :acquire 3000
                                 :evict 10000}}))]
    (-> (.authenticate sequelize)
        (.then (fn [] sequelize)))))

(defn reinit-sequelize [^js sequelize]
  (.initPools (.. sequelize -connectionManager))
  (when (.hasOwnProperty (.. sequelize -connectionManager) "getConnection")
    (js-delete (.. sequelize -connectionManager) "getConnection")))

(defn open-sequelize []
  (p/let [loaded (some? @!sequelize)
          sequelize (or @!sequelize (load-sequelize))
          _ (reset! !sequelize sequelize)
          _ (when loaded (reinit-sequelize sequelize))]
    sequelize))

(defn close-sequelize [^js sequelize]
  (.close (.. sequelize -connectionManager)))
