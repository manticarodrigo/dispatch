(ns ui.components.inputs.loading-button
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.button :refer (button)]))

(defn loading-button [{:keys [loading disabled type label class on-click]}]
  [button
   {:type type
    :label (if loading
             [:span {:class "flex justify-center items-center"}
              [spinner {:class "mr-2 w-5 h-5"}] (tr [:misc/loading]) "..."]
             label)
    :class (class-names class (when loading "cursor-progress"))
    :disabled (or loading disabled)
    :on-click on-click}])
