(ns ui.components.inputs.locale-radio-group
  (:require
   [re-frame.core :refer (dispatch)]
   [ui.subs :refer (listen)]
   [ui.utils.i18n :refer (locales)]
   [ui.components.inputs.generic.radio-group :refer (radio-group)]))

(def ^:private locale-options
  [{:key "en" :label "EN" :value (:en-US locales)}
   {:key "es" :label "ES" :value (:es-ES locales)}])

(defn- change-locale [key]
  (dispatch [:locale/set (->>
                          locale-options
                          (filter #(= (:key %) key))
                          (first)
                          (:value))]))

(defn locale-radio-group []
  [radio-group
   {:sr-label "Select language"
    :value (listen [:locale/language])
    :options locale-options
    :on-change change-locale}])
