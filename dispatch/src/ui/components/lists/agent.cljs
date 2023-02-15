(ns ui.components.lists.agent
  (:require [react-feather :rename {User UserIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn agent-list [{:keys [agents loading]}]
  [:<>
   [:ul
    (doall
     (for [{:keys [id name location]} agents]
       (let [{:keys [createdAt]} location
             active? (and createdAt (d/isAfter createdAt (d/subHours (js/Date.) 26)))]
         ^{:key id}
         [:li {:class "mb-2"}
          [link-card {:to id
                      :icon UserIcon
                      :title name
                      :subtitle (tr [:status/last-seen] [createdAt])
                      :detail [status-detail
                               {:active? active?
                                :text (if active?
                                        (tr [:status/active])
                                        (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? agents)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
