(ns app.core
  (:require
   [reagent.dom :as dom]
   [re-frame.core :as rf]
   [app.config :as config]
   [app.events :as events]
   [app.views.map :as map-view]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (dom/unmount-component-at-node root-el)
    (dom/render [map-view/page] root-el)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
