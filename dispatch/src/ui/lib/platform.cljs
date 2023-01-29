(ns ui.lib.platform
  (:require ["@capacitor/core" :refer (Capacitor)]))

(def platform (.getPlatform Capacitor))
