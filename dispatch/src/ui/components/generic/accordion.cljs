(ns ui.components.generic.accordion
  (:require [reagent.core :as r]
            [react-feather :rename
             {ChevronDown ChevronDownIcon
              ChevronUp ChevronUpIcon}]
            ["@headlessui/react" :refer (Disclosure)]
            [ui.utils.string :refer (class-names)]
            [ui.components.generic.button :refer (button-class)]))

(def ^:private Button (.-Button Disclosure))
(def ^:private Panel (.-Panel Disclosure))

(defn accordion [{items :items
                  item-class :item-class}]
  [:<>
   (doall
    (for [[idx item] (map-indexed vector items)]
      [:> Disclosure {:key (or (:key item) idx)}
       (fn [props]
         (r/as-element
          [:<>
           [:> Button {:class (class-names item-class button-class)}
            [:div {:class "flex justify-between"}
             (:name item)
             [:span {:class "flex items-center flex-shrink-0"}
              (if (.-open props) [:> ChevronUpIcon] [:> ChevronDownIcon])]]]
           [:> Panel (:description item)]]))]))])
