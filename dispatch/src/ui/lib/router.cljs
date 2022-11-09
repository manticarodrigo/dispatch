(ns ui.lib.router
  (:require
   ["react-router-dom"
    :refer (BrowserRouter
            MemoryRouter
            Routes
            Route
            Navigate
            NavLink
            Link
            useNavigate)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj)]
   [ui.utils.cookie :refer (get-session remove-session)]))

(defn browser-router [& children]
  [:> BrowserRouter
   (into [:<>] children)])

(defn memory-router [props & children]
  [:> MemoryRouter props
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
