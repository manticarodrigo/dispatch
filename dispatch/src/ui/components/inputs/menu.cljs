(ns ui.components.inputs.menu
  (:require [headlessui-reagent.core :as ui]
            [ui.lib.router :refer (link)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button-class)]))

(def menu-class (class-names
                 "rounded-md border border-neutral-700"
                 "backdrop-blur bg-neutral-800/75 shadow-lg"
                 "overflow-y-auto"))

(def menu-item-class "cursor-pointer relative block px-4 py-3 text-sm")
(def menu-item-active-class "after:z-[-1] after:block after:absolute after:rounded after:inset-1 after:bg-neutral-700/50")

(defn menu-item [{:keys [component label to]} item-class]
  [ui/menu-item
   (fn [{:keys [active]}]
     (if component
       [:div {:class (class-names
                      item-class
                      menu-item-class)}
        component]
       [:div
        [link {:to to
               :class (class-names
                       item-class
                       menu-item-class
                       (when active menu-item-active-class))}

         label]]))])

(defn menu [{label :label
             items :items
             class-map :class-map}
            & children]
  (let [{button-class! :button!
         item-class :item} class-map]
    [ui/menu {:as "div" :class (class-names "z-10 relative inline-flex")}
     [ui/menu-button {:class (or button-class! button-class)} label]
     [ui/menu-items {:as "div"
                     :class (class-names
                             menu-class
                             "absolute right-0 top-[100%] origin-top-right"
                             "mt-2"
                             "divide-y divide-neutral-700"
                             "overflow-y-auto")}
      [:<>
       (for [[idx component] (map-indexed vector children)]
         ^{:key idx}
         [:div {:class "w-full"} [menu-item {:component component} item-class]])]
      [:<>
       (for [[idx item] (map-indexed vector items)]
         ^{:key idx}
         [:<>
          (if (vector? item)
            [:div {:class "w-full"}
             (for [[sub-idx sub-item] (map-indexed vector item)]
               ^{:key (str idx "-" sub-idx)}
               [menu-item sub-item item-class])]
            [menu-item item item-class])])]]]))
