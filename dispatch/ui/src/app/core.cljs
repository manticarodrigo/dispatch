(ns app.core
  (:require
   [reagent.dom :as dom]
   [re-frame.core :as rf]
   [app.config :as config]
   [app.events :as events]
   [app.views.route.core :refer (route-view)]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (dom/unmount-component-at-node root-el)
    (dom/render [:f> route-view] root-el)))

(defn init []
  (js/console.log config/API_DOMAIN)
  (-> (js/fetch (str "https://" config/API_DOMAIN "/graph"))
      (.then #(js/console.log %)))
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
