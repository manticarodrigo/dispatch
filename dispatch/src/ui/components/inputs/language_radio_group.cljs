(ns ui.components.inputs.language-radio-group
  (:require
   [re-frame.core :refer (dispatch)]
   [ui.subs :refer (listen)]
   [ui.components.inputs.radio-group :refer (radio-group)]))

(def ^:private language-options
  [{:key "en" :label "EN"}
   {:key "es" :label "ES"}])

(defn language-radio-group []
  [radio-group
   {:sr-label "Select language"
    :value (listen [:language])
    :options language-options
    :on-change #(dispatch [:language %])}])
