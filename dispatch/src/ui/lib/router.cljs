(ns ui.lib.router
  (:require
   ["@apollo/client" :refer (useApolloClient)]
   [react-router-dom
    :refer (BrowserRouter
            Routes
            Route
            Navigate
            NavLink
            Link
            useNavigate
            useParams
            useSearchParams)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [ui.subs :refer (listen)]
   [ui.utils.session :refer (remove-session)]))

(defn browser-router [& children]
  [:> BrowserRouter
   (into [:<>] children)])

(defn routes [& routes]
  [:> Routes
   (for [[path hiccup] routes]
     [:> Route {:key path
                :path path
                :element (r/as-element hiccup)}])])

(defn auth-route [route]
  (let [session-id (listen [:session])]
    (if session-id
      route
      [:> Navigate {:to "/login" :replace true}])))

(defn remove-auth-route []
  (let [^js client (useApolloClient)]
    (remove-session)
    (.resetStore client)
    [:> Navigate {:to "/login" :replace true}]))

(defn use-navigate []
  (useNavigate))

(defn use-params []
  (let [params (useParams)]
    (->clj params)))

(defn use-search-params []
  (let [[params set-params] (useSearchParams)]
    [(->> params (js/URLSearchParams.) (.fromEntries js/Object) ->clj)
     #(-> % ->js set-params)]))

(defn nav-link [{to :to class-fn :class} & children]
  (into [:> NavLink {:to to
                     :class (fn [props]
                              (class-fn (->clj props)))}]
        children))

(defn link [{to :to class :class} & children]
  (into [:> Link {:to to :class class}]
        children))
