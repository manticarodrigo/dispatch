(ns ui.components.controls
  (:require
   [ui.subs :refer (listen)]
   [ui.hooks.use-route :refer (use-route-context)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.button :refer (button)]
   [ui.components.inputs.generic.combobox :refer (combobox)]))

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
