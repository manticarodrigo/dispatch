(ns ui.views.route.nav.header
  (:require
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [react-feather
    :refer (ChevronUp)
    :rename {ChevronUp ChevronUpIcon}]
   [ui.utils.i18n :refer (locales)]
   [ui.utils.string :refer (class-names)]
   [ui.components.generic.radio-group :refer (radio-group)]
   [ui.views.route.utils :refer (padding-x)]
   [ui.views.route.nav.summary :refer (!summary-open)]))

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
  [:div {:class (class-names
                 class
                 padding-x
                 "h-[60px]"
                 "grid grid-cols-3 lg:grid-cols-2 items-center")}
   [:h1 {:class (class-names "font-semibold text-sm sm:text-base lg:text-xl")}
    "Ambito " [:span {:class "font-light text-neutral-50"} "Dispatch"]]
   [:button {:class "lg:hidden flex justify-center"
             :on-click #(swap! !summary-open not)} [:> ChevronUpIcon {:size 40}]]
   [:div {:class "flex justify-end"}
    [radio-group
     {:sr-label "Select language"
      :value @!selected-locale-key
      :options locale-options
      :on-change change-locale}]]])
