(ns ui.lib.router
  (:require
   ["react-router-dom"
    :refer (BrowserRouter
            Routes
            Route)]
   [reagent.core :as r]))

(defn browser-router [& children]
  [:> BrowserRouter
   (into [:<>] children)])

(defn routes [& routes]
  [:> Routes
   (for [[path hiccup] routes]
     [:> Route {:key path
                :path path
                :element (r/as-element hiccup)}])])
