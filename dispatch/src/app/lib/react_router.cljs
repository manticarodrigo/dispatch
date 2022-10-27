(ns app.lib.react-router
  (:require
   ["react-router-dom" :refer (Routes Route Navigate) :rename {BrowserRouter Router}]
   [reagent.core :as r]
   [app.utils.cookie :refer (get-session)]))

(defn router [& children]
  [:> Router
   (into [:<>] children)])

(defn routes [& routes]
  [:> Routes
   (for [[path hiccup] routes]
     [:> Route {:key path
                :path path
                :element (r/as-element hiccup)}])])

(defn route-auth-wrap [route]
  (if (get-session)
    route
    [:> Navigate {:to "/login" :replace true}]))
