(ns schema
  (:require
   ["sequelize" :refer (DataTypes)]
   [cljs-bean.core :refer (->js)]
   [config]))

(defn user [sequelize]
  (.define sequelize "User"
           (->js {:id {:type (.-UUID DataTypes)
                       :defaultValue (.-UUIDV4 DataTypes)
                       :primaryKey true
                       :allowNull false}
                  :sessions {:type (.ARRAY DataTypes (.-TEXT DataTypes))}
                  :email {:type (.-TEXT DataTypes)
                          :allowNull false
                          :unique true
                          :validate {:len [6]}}
                  :password {:type (.-TEXT DataTypes)
                             :allowNull false
                             :validate {:len [1]}}
                  :firstName {:type (.-TEXT DataTypes)
                              :allowNull false
                              :validate {:len [1]}}
                  :lastName {:type (.-TEXT DataTypes)
                             :allowNull false
                             :validate {:len [1]}}})))
