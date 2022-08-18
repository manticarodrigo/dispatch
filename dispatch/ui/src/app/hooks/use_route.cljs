(ns app.hooks.use-route
  (:require
   [react]
   [app.hooks.use-maps :refer (use-maps)]
   [app.hooks.use-location :refer (use-location)]))

(defonce ^:private route-context (react/createContext {}))

(defn use-route-context []
  (let [val (react/useContext route-context)]
    (js->clj val :keywordize-keys true)))

(def route-context-provider (.-Provider route-context))

(defn use-route []
  (let [{!el :ref
         search-places! :search
         set-origin! :origin} (use-maps)
        {get-position :get
         watch-position :watch} (use-location)]
    {:ref !el
     :search search-places!
     :origin set-origin!
     :get get-position
     :watch watch-position}))
