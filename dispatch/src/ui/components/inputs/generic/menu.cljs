(ns ui.components.inputs.generic.menu
  (:require [reagent.core :as r]
            ["@headlessui/react" :refer (Menu)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.button :refer (button-class)]))

(def ^:private Button (.-Button Menu))
(def ^:private Items (.-Items Menu))
(def ^:private Item (.-Item Menu))

(defn menu [{label :label
             items :items
             class-map :class-map}]
  (let [{button-class! :button!
         item-class :item} class-map]
    [:> Menu {:as "div" :class (class-names "z-20 relative inline-block")}
     [:> Button {:class (or button-class! button-class)} label]
     [:> Items {:as "ul" :class (class-names
                                 "absolute right-0 origin-top-right"
                                 "mt-2 rounded border border-neutral-500"
                                 "divide-y divide-neutral-700"
                                 "text-neutral-100 bg-neutral-800"
                                 "shadow-lg overflow-hidden")}
      (doall
       (for [[idx item] (map-indexed vector items)]
         [:> Item {:key (or (:key item) idx)}
          (fn [props]
            (r/as-element
             [:li {:class (class-names
                           item-class
                           "p-2 w-full cursor-pointer"
                           (when (. props -active) "bg-neutral-900"))}
              (:label item)]))]))]]))
