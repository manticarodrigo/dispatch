(ns ui.components.lists.task
  (:require [react-feather :rename {Clipboard TaskIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))

(defn task-list [{:keys [tasks loading]}]
  [:div {:class "overflow-y-auto"}
   [:ul
    (doall
     (for [{:keys [id agent startAt]} tasks]
       (let [{:keys [name]} agent
             started? (d/isBefore startAt (js/Date.))]
         ^{:key id}
         [:li
          [link-card {:to (str "../tasks/" id)
                      :icon TaskIcon
                      :title (or name (tr [:status/start-at] [startAt]))
                      :subtitle (d/format startAt "dd/MM/yyyy hh:mmaaa")
                      :detail [status-detail {:active? started?
                                              :text (if started?
                                                      (tr [:status/active])
                                                      (tr [:status/inactive]))}]}]])))]
   (if loading
     [:p {:class "text-center"} (tr [:misc/loading]) "..."]
     (when (empty? tasks)
       [:p {:class "text-center"} (tr [:misc/empty-search])]))])
