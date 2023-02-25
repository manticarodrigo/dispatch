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
                  "agent/:id/tasks"
                  "agent/:id/places"])

(def index-routes (mapv (fn [path] {:path path :element [dispatch-icon]}) index-paths))

(def routes (conj index-routes {:path "*" :element [back-button]}))

(defn button []
  (use-routes routes))
