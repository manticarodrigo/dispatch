(ns ui.components.lists.place
  (:require [react-feather :rename {MapPin PinIcon}]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn place-list [{:keys [places loading]}]
  [:<>
   [:ul
    (doall
     (for [{:keys [id name description]} places]
       (let [active? false]
         ^{:key id}
         [:li {:class "mb-2"}
          [link-card {:to id
                      :icon PinIcon
                      :title name
                      :subtitle description
                      ;; :detail [status-detail
                      ;;          {:active? active?
                      ;;           :text (if active?
                      ;;                   (tr [:status/active])
                      ;;                   (tr [:status/inactive]))}]
                      }]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? places)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])