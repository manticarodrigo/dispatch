(ns ui.lib.router
  (:require
   ["react-router-dom"
    :refer (Routes Route Navigate useNavigate NavLink Link)
    :rename {BrowserRouter Router}]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj)]
   [ui.utils.cookie :refer (get-session remove-session)]))

(defn router [& children]
  [:> Router
   (into [:<>] children)])

(defn routes [& routes]
  [:> Routes
   (for [[path hiccup] routes]
     [:> Route {:key path
                :path path
                :element (r/as-element hiccup)}])])

(defn auth-route [route]
  (if (get-session)
    route
    [:> Navigate {:to "/login" :replace true}]))

(defn remove-auth-route []
  (remove-session)
  [:> Navigate {:to "/login" :replace true}])

(defn use-navigate []
  (useNavigate))

(defn nav-link [{to :to class-fn :class} & children]
  (into [:> NavLink {:to to
                     :class (fn [props]
                              (class-fn (->clj props)))}]
        children))

(defn link [{to :to class :class} & children]
  (into [:> Link {:to to :class class}]
        children))
