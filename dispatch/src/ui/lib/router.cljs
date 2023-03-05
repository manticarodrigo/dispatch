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
            Outlet
            useRoutes
            useNavigate
            useLocation
            useParams
            useSearchParams)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [ui.subs :refer (listen)]
   [ui.utils.session :refer (remove-session)]))

(defn browser-router [& children]
  [:> BrowserRouter
   (into [:<>] children)])

(defn transform-routes [routes]
  (map (fn [item]
         (let [new-item (assoc item :element (r/as-element (:element item)))]
           (if (:children item)
             (assoc new-item :children (transform-routes (:children item)))
             new-item)))
       routes))

(defn use-routes [routes]
  (useRoutes (->js (transform-routes routes))))

(defn routes [& routes]
  [:> Routes
   (for [[path hiccup] routes]
     [:> Route {:key path
                :path path
                :element (r/as-element hiccup)}])])

(defn navigate [to]
  [:> Navigate {:to to}])

(defn outlet []
  [:> Outlet])

(defn auth-route [route]
  (let [session-id (listen [:session])]
    (if session-id
      route
      [navigate "/login"])))

(defn remove-auth-route []
  (let [^js client (useApolloClient)]
    (remove-session)
    (.resetStore client)
    [navigate "/login"]))

(defn use-navigate []
  (useNavigate))

(defn use-window-location []
  (let [location (some-> js/window .-location)]
    {:protocol (.-protocol location)
     :host (.-host location)
     :hostname (.-hostname location)
     :port (.-port location)
     :pathname (.-pathname location)
     :search (.-search location)
     :hash (.-hash location)
     :origin (.-origin location)}))

(defn use-params []
  (let [params (useParams)]
    (->clj params)))

(defn use-location []
  (let [location (useLocation)]
    (->clj location)))

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
