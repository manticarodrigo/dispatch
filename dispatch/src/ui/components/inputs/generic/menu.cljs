(ns ui.components.inputs.generic.menu
  (:require [reagent.core :as r]
            ["@headlessui/react" :refer (Menu)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.generic.button :refer (button-class)]))

(def ^:private Button (.-Button Menu))
(def ^:private Items (.-Items Menu))
(def ^:private Item (.-Item Menu))

(defn menu-item [idx item item-class]
  [:> Item {:key (or (:key item) idx)}
   (fn [props]
     (r/as-element
      [:li {:class (class-names item-class "p-1.5 w-full cursor-pointer")}
       [:div {:class (class-names
                      "rounded p-1.5 px-2 w-full"
                      (when (. props -active) "bg-neutral-700"))}
        (:label item)]]))])

(defn menu [{label :label
             items :items
             class-map :class-map}]
  (let [{button-class! :button!
         item-class :item} class-map]
    [:> Menu {:as "div" :class (class-names "z-20 relative inline-block")}
     [:> Button {:class (or button-class! button-class)} label]
     [:> Items {:as "ul" :class (class-names
                                 "absolute right-0 origin-top-right"
                                 "mt-2 rounded-md border border-neutral-500"
                                 "divide-y divide-neutral-700"
                                 "text-neutral-100 bg-neutral-900"
                                 "shadow-lg overflow-hidden")}
      (doall
       (for [[idx item] (map-indexed vector items)]
         (if (vector? item)
           [:div {:class "w-full"}
            (for [[idx2 item2] (map-indexed vector item)]
              [menu-item idx2 item2 item-class])]
           [menu-item idx item item-class])))]]))
