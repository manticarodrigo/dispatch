(ns ui.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   [re-frame.core :as rf]
   [ui.events :as events]
   [ui.lib.apollo :refer (apollo-provider)]
   [ui.components.routes :refer (routes)]))

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn app [] [apollo-provider [routes]])

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rd/unmount-component-at-node root-el)
    (rd/render [app] root-el)))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (mount-root))
