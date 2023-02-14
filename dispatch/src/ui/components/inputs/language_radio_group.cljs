(ns ui.components.inputs.language-radio-group
  (:require
   [re-frame.core :refer (dispatch)]
   [ui.subs :refer (listen)]
   [ui.utils.i18n :refer (tr)]
   [ui.components.inputs.radio-group :refer (radio-group)]))

(def ^:private language-options
  [{:key "en" :label "EN"}
   {:key "es" :label "ES"}])

(defn language-radio-group []
  [radio-group
   {:sr-label (tr [:field/language])
    :value (listen [:language])
    :options language-options
    :on-change #(dispatch [:language %])}])
