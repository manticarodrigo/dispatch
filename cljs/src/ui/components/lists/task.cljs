(ns ui.components.lists.task
  (:require ["react-feather" :rename {Clipboard TaskIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]
            [ui.components.status-detail :refer (status-detail)]))

(defn task-list [{:keys [tasks loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id agent startAt]}]
              (let [{:keys [name]} agent
                    started? (d/isBefore startAt (js/Date.))]
                {:id id
                 :to (str "../tasks/" id)
                 :icon TaskIcon
                 :title (or name (tr [:status/start-at] [startAt]))
                 :subtitle (d/format startAt "dd/MM/yyyy hh:mmaaa")
                 :detail [status-detail
                          {:active? started?
                           :text (if started?
                                   (tr [:status/active])
                                   (tr [:status/inactive]))}]}))
            tasks)}])
