(ns app.views.route.nav.header
  (:require
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [react-feather
    :refer (Menu)
    :rename {Menu MenuIcon}]
   [app.utils.i18n :refer (locales)]
   [app.utils.string :refer (class-names)]
   [app.components.generic.radio-group :refer (radio-group)]
   [app.views.route.utils :refer (padding-x)]
   [app.views.route.nav.summary :refer (!summary-open)]))

(def ^:private locale-options
  [{:key "en" :label "EN" :value (:en-US locales)}
   {:key "es" :label "ES" :value (:es-ES locales)}])

(def ^:private !selected-locale-key (r/atom (-> locale-options first :key)))

(defn- change-locale [key]
  (reset! !selected-locale-key key)
  (dispatch [:locale/set (->>
                          locale-options
                          (filter #(= (:key %) key))
                          (first)
                          (:value))]))

(defn header [class]
  [:div {:class (class-names class padding-x "flex justify-between items-center h-[60px]")}
   [:button {:class "lg:hidden mr-2"
             :on-click #(swap! !summary-open not)} [:> MenuIcon {:size 20}]]
   [:h1 {:class (class-names "font-semibold text-xl")}
    "Ambito " [:span {:class "font-light text-neutral-50"} "Dispatch"]]
   [radio-group
    {:sr-label "Select language"
     :value @!selected-locale-key
     :options locale-options
     :on-change change-locale}]])
