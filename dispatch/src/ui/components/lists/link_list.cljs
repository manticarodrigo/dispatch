(ns ui.components.lists.link-list
  (:require ["react-feather" :rename {ChevronRight ChevronRightIcon}]
            [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]))

(defn link-card [{:keys [to decorator icon title subtitle detail]}]
  [link {:to to
         :class (class-names
                 "block"
                 "pl-4 pr-3 py-1"
                 "hover:bg-neutral-800 focus:bg-neutral-800 active:bg-neutral-800")}
   [:div {:class "flex items-center justify-between w-full text-left"}
    [:div (or decorator [:> icon {:class "w-4 h-4"}])]
    [:div {:class "px-4 w-full truncate"}
     [:div {:class "font-medium text-sm truncate"} title]
     [:div {:class "font-light text-xs text-neutral-400 truncate"} subtitle]]
    [:div {:class "flex-shrink-0 flex items-center"}
     [:div {:class "flex flex-col items-end"} detail]
     [:div {:class "ml-2"} [:> ChevronRightIcon {:class "w-4 h-4"}]]]]])

(defn link-list [{:keys [type loading items]}]
  [:div {:class "overflow-y-auto"}
   [(or type :ul) {:class "divide-y divide-neutral-800"}
    (doall
     (for [{:keys [id to decorator icon title subtitle detail]} items]
       ^{:key id}
       [:li
        [link-card {:to (or to id)
                    :decorator decorator
                    :icon icon
                    :title title
                    :subtitle subtitle
                    :detail detail}]]))]
   (if loading
     [:p {:class "p-4 text-center"} (tr [:misc/loading]) "..."]
     (when (empty? items)
       [:p {:class "p-4 text-center"} (tr [:misc/empty-search])]))])
