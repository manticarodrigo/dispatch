(ns ui.components.inputs.submit-button
  (:require [ui.utils.string :refer (class-names)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.button :refer (button)]))

(defn submit-button [{:keys [loading]}]
  [button
   {:label (if loading
             [:span {:class "flex justify-center items-center"}
              [spinner {:class "mr-2 w-5 h-5"}] (tr [:generic/loading]) "..."]
             (tr [:field/submit]))
    :class (class-names "my-4 w-full" (when loading "cursor-progress"))
    :disabled loading}])
