(ns ui.components.nav
  (:require
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [react-feather
    :refer (ChevronUp)
    :rename {ChevronUp ChevronUpIcon}]
   [ui.utils.i18n :refer (locales)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]
   [ui.components.generic.radio-group :refer (radio-group)]))

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

(defn nav [& children]
  [:<>
   [:div {:class (class-names
                  "relative z-20"
                  "grid grid-cols-3 lg:grid-cols-2 items-center"
                  "border-b border-neutral-600"
                  "w-full h-[60px]"
                  padding-x)}
    [:h1 {:class (class-names "font-semibold text-white text-sm sm:text-base lg:text-xl")}
     "Ambito " [:span {:class "font-light text-neutral-50"} "Dispatch"]]
    [:button {:class "lg:hidden flex justify-center"} [:> ChevronUpIcon {:size 40}]]
    [:div {:class "flex justify-end"}
     [radio-group
      {:sr-label "Select language"
       :value @!selected-locale-key
       :options locale-options
       :on-change change-locale}]]]
   (into [:div {:class "w-full h-[calc(100%_-_60px)]"} children])])
