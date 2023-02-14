(ns ui.components.lists.seat
  (:require [react-feather :rename {User UserIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn seat-list [{:keys [seats loading]}]
  [:<>
   [:ul
    (doall
     (for [{:keys [id name location]} seats]
       (let [{:keys [createdAt]} location
             location-date (some-> createdAt js/parseInt js/Date.)
             active? (and location-date (d/isAfter location-date (d/subHours (js/Date.) 26)))]
         ^{:key id}
         [:li {:class "mb-2"}
          [link-card {:to id
                      :icon UserIcon
                      :title name
                      :subtitle (tr [:status/last-seen] [location-date])
                      :detail [status-detail
                               {:active? active?
                                :text (if active?
                                        (tr [:status/active])
                                        (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? seats)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
