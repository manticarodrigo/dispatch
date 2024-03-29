(ns ui.utils.color)

(def color-map {:red "#ef4444"
                :orange "#f97316"
                :amber "#f59e0b"
                :yellow "#eab308"
                :lime "#84cc16"
                :green "#22c55e"
                :emerald "#10b981"
                :teal "#14b8a6"
                :cyan "#06b6d4"
                :sky "#0ea5e9"
                :blue "#3b82f6"
                :indigo "#6366f1"
                :violet "#8b5cf6"
                :purple "#a855f7"
                :fuchsia "#d946ef"
                :pink "#ec4899"
                :rose "#f43f5e"})

(def color-list [(:sky color-map)
                 (:emerald color-map)
                 (:fuchsia color-map)
                 (:rose color-map)
                 (:violet color-map)
                 (:indigo color-map)
                 (:blue color-map)
                 (:cyan color-map)
                 (:teal color-map)
                 (:green color-map)
                 (:lime color-map)
                 (:yellow color-map)
                 (:amber color-map)
                 (:orange color-map)
                 (:red color-map)
                 (:pink color-map)
                 (:purple color-map)])

(defn get-color [idx]
  (nth color-list (mod idx (count color-list))))
