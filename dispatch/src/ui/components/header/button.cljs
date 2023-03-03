(ns ui.components.header.button
  (:require [ui.lib.router :refer (use-routes)]
            [ui.components.inputs.back-button :refer (back-button)]
            [ui.components.icons.dispatch :rename {dispatch dispatch-icon}]))

(def index-paths ["register"
                  "login"
                  "logout"
                  "organization/tasks"
                  "organization/agents"
                  "organization/places"
                  "organization/vehicles"
                  "organization/shipments"
                  "organization/plans"
                  "agent/tasks"
                  "agent/places"])

(def index-routes (mapv (fn [path] {:path path :element [dispatch-icon {:class "w-4 h-4"}]}) index-paths))

(def routes (conj index-routes {:path "*" :element [back-button]}))

(defn button []
  (use-routes routes))
