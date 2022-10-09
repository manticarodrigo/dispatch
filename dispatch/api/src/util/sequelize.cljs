(ns util.sequelize
  (:require ["sequelize" :refer (Sequelize DataTypes)]
            [cljs-bean.core :refer (->js)]
            [promesa.core :as p]
            [config]
            [util.anom :as anom]))

(def !sequelize (atom nil))


(defn sync-sequelize [sequelize]
  (-> (.query sequelize "CREATE EXTENSION IF NOT EXISTS postgis;")
      (.then (.sync sequelize #js{:alter true}))))

(defn load-sequelize [init-models]
  (let [sequelize (Sequelize.
                   (str
                    "postgres://"
                    config/PGUSER
                    ":"
                    config/PGPASSWORD
                    "@"
                    config/PGHOST
                    ":"
                    config/PGPORT
                    "/"
                    config/PGDATABASE)
                   (->js {:pool {:max 2
                                 :min 0
                                 :idle 0
                                 :acquire 3000
                                 :evict 10000}}))]
    (init-models sequelize)
    (-> (sync-sequelize sequelize)
        (.then (fn [] sequelize)))))

(defn reinit-sequelize [sequelize]
  (.initPools (.. sequelize -connectionManager))
  (when (.hasOwnProperty (.. sequelize -connectionManager) "getConnection")
    (js-delete (.. sequelize -connectionManager) "getConnection")))

(defn open-sequelize [init-models]
  (p/let [loaded (some? @!sequelize)
          sequelize (or @!sequelize (load-sequelize init-models))
          _ (reset! !sequelize sequelize)
          _ (when loaded (reinit-sequelize sequelize))]))

(defn close-sequelize []
  (when @!sequelize (.close (.. @!sequelize -connectionManager))))

(defn gen-id []
  {:type (.-UUID DataTypes)
   :defaultValue (.-UUIDV4 DataTypes)
   :primaryKey true
   :allowNull false})

(defn parse-error
  "returns http status code and anom map tuple"
  [e]
  (let [name (.-name e)
        errors (.-errors e)
        mapped-errors (mapv
                       (fn [r]
                         {:message (.-message r)
                          :path (.-path r)
                          :value (.-value r)
                          :validatorKey (.-validatorKey r)})
                       errors)]
    (cond
      (= name "SequelizeUniqueConstraintError")
      [409 (anom/conflict :unique-constraint-error mapped-errors)]
      (= name "SequelizeValidationError")
      [400 (anom/incorrect :validation-error mapped-errors)
       :else
       [500 (anom/fault :unknown-error)]])))

(defn append
  "takes model name, column name, value, and where clause"
  [m c v w]
  (.update m
           (->js (assoc {} (keyword c) (.fn @!sequelize "array_append" (.col @!sequelize c) v)))
           (->js {:where w})))
