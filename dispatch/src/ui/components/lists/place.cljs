(ns ui.components.lists.place
  (:require [react-feather :rename {MapPin PinIcon}]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]))


(defn place-list [{:keys [places loading]}]
  [:div {:class "overflow-y-auto"}
   [:ul
    (doall
     (for [{:keys [id name description]} places]
       ^{:key id}
       [:li
        [link-card {:to id
                    :icon PinIcon
                    :title name
                    :subtitle description}]]))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? places)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
