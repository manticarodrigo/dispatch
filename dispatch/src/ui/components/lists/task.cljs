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
     (for [{:keys [id seat startAt]} tasks]
       (let [{:keys [name]} seat
             start-date (some-> startAt js/parseInt js/Date.)
             started? (and start-date (d/isBefore start-date (js/Date.)))]
         ^{:key id}
         [:li {:class "mb-2"}
          [link-card {:to (str "../tasks/" id)
                      :icon RouteIcon
                      :title (or name
                                 (str (if started? (tr [:status/started]) (tr [:status/starts]))
                                      " "
                                      (d/formatDistanceToNowStrict start-date)))
                      :subtitle (-> start-date (d/format "yyyy/MM/dd hh:mmaaa"))
                      :detail [status-detail {:active? started?
                                              :text (if started?
                                                      (tr [:status/active])
                                                      (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? tasks)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
