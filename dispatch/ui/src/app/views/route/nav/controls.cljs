(ns app.views.route.nav.controls
  (:require
   [app.subs :refer (listen)]
   [app.hooks.use-route :refer (use-route-context)]
   [app.utils.i18n :refer (tr)]
   [app.utils.string :refer (class-names)]
   [app.components.button :refer (button)]
   [app.components.input :refer (input)]
   [app.views.route.utils :refer (padding)]))

(def ^:private label #(% {:search-address (tr [:location/search])
                          :get-position (tr [:location/get])
                          :watch-position (tr [:location/watch])}))

(defn controls [class]
  (let [origin (listen [:origin])
        location (listen [:location])
        {get-position :get watch-position :watch search-address :search} (use-route-context)]
    [:div {:class (class-names class padding "grid grid-cols-2 gap-4")} 
     [:<>
      (when (nil? origin)
        [:<>
         [input {:label (label :search-address)
                 :class "col-span-2"
                 :on-change search-address}]
         [button {:label (label :get-position)
                  :class "col-span-2"
                  :on-click get-position}]])
      (when (and (some? origin) (nil? location))
        [button {:label (label :watch-position)
                 :class "col-span-2"
                 :on-click watch-position}])]]))
