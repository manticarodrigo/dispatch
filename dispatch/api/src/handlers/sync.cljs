(ns handlers.sync
  (:require [model.user]
            [util.sequelize]))

(defn sync
  [_ res]
  (-> (util.sequelize/sync) (.then (-> res (.send "Sync succeeded!")))))
