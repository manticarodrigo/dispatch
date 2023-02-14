(ns ui.components.inputs.submit-button
  (:require [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.loading-button :refer (loading-button)]))

(defn submit-button [{:keys [loading]}]
  [loading-button
   {:loading loading
    :label (tr [:field/submit])
    :class "my-4 w-full"}])
