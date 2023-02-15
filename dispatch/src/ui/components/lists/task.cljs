(ns ui.components.lists.task
  (:require [react-feather :rename {GitPullRequest RouteIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))


(defn task-list [{:keys [tasks loading]}]
  [:<>
   [:ul
    (doall
     (for [{:keys [id agent startAt]} tasks]
       (let [{:keys [name]} agent
             started? (d/isBefore startAt (js/Date.))]
         ^{:key id}
         [:li {:class "mb-2"}
          [link-card {:to (str "../tasks/" id)
                      :icon RouteIcon
                      :title (or name (tr [:status/start-at] [startAt]))
                      :subtitle (d/format startAt "yyyy/MM/dd hh:mmaaa")
                      :detail [status-detail {:active? started?
                                              :text (if started?
                                                      (tr [:status/active])
                                                      (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? tasks)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
