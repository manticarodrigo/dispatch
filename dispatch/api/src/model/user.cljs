(ns model.user
  (:require
   ["sequelize" :refer (DataTypes)]
   [cljs-bean.core :refer (->js)]
   [util.sequelize :refer (sequelize gen-id)]))

(def User
  (.define sequelize "User"
           (->js {:id (gen-id)
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
                             :validate {:len [1]}}
                  :imageUrl (.-TEXT DataTypes)
                  :location (.GEOGRAPHY DataTypes "POINT")})))
