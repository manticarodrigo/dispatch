(ns ui.lib.router
  (:require
   ["next/link" :as Link]
   ["next/navigation" :refer (useRouter usePathname useSearchParams useParams)]
   ["@apollo/client" :refer (useApolloClient)]
   ["react-router-dom" :refer (BrowserRouter
                               Routes
                               Route
                               Navigate
                               Outlet
                               useRoutes)]
   ["react-router-dom/server" :refer (StaticRouter)]
   [reagent.core :as r]
   [cljs-bean.core :refer (->clj ->js)]
   [ui.subs :refer (listen)]
   [ui.utils.session :refer (remove-session)]))

(defn static-router [& children]
  [:> StaticRouter
   (into [:<>] children)])

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
  (let [router (useRouter)]
    #(.push router %)))

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

(defn use-pathname []
  (usePathname))

(defn use-search-params []
  (let [navigate (use-navigate)
        pathname (use-pathname)
        params (useSearchParams)
        search-params (->> params (.fromEntries js/Object) ->clj)
        set-search-params (fn [next-params]
                            (let [current (js/URLSearchParams.)
                                  _ (doseq [[k v] next-params]
                                      (if v
                                        (.set current (name k) (.trim v))
                                        (.delete current (name k))))
                                  search (.toString current)
                                  query (if (empty? search) "" (str "?" search))
                                  route (str pathname query)]
                              (navigate route)))]
    [search-params set-search-params]))

(defn nav-link [{to :to class-fn :class} & children]
  (let [pathname (use-pathname)
        active? (= to pathname)]
    (into [:> Link {:href to
                    :class (class-fn {:isActive active?})}]
          children)))

(defn link [{to :to class :class} & children]
  (into [:> Link {:href to :class class}]
        children))
