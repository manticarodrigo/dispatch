(ns app.views.route.nav.controls
  (:require
   [app.subs :refer (listen)]
   [app.hooks.use-route :refer (use-route-context)]
   [app.utils.i18n :refer (tr)]
   [app.utils.string :refer (class-names)]
   [app.components.generic.button :refer (button)]
   [app.components.generic.combobox :refer (combobox)]
   [app.views.route.utils :refer (padding)]))

(defn controls [class]
  (let [origin (listen [:origin])
        location (listen [:location])
        search (listen [:search])
        {get-position :get
         watch-position :watch
         search-address :search
         set-origin :origin}
        (use-route-context)]
    (when-not (and origin location)
      [:div {:class (class-names class padding "grid grid-cols-2 gap-4")}
       [:<>
        (if-not origin
          [:<>
           [combobox {:label (tr [:location/search])
                      :class "col-span-2"
                      :options search
                      :option-to-label #(:description %)
                      :option-to-value #(:place_id %)
                      :on-text search-address
                      :on-change set-origin}]
           [button {:label (tr [:location/get])
                    :class "col-span-2"
                    :on-click get-position}]]

          [button {:label (tr [:location/watch])
                   :class "col-span-2"
                   :on-click watch-position}])]])))
