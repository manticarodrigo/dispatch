(ns ui.components.lists.agent
  (:require ["react-feather" :rename {User UserIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.lists.link-list :refer (link-list)]
            [ui.components.status-detail :refer (status-detail)]))

(defn agent-list [{:keys [agents loading]}]
  [link-list
   {:loading loading
    :items (map
            (fn [{:keys [id name location]}]
              (let [{:keys [createdAt]} location
                    active? (and createdAt (d/isAfter createdAt (d/subHours (js/Date.) 26)))]
                {:id id
                 :icon UserIcon
                 :title name
                 :subtitle (tr [:status/last-seen] [createdAt])
                 :detail [status-detail
                          {:active? active?
                           :text (if active?
                                   (tr [:status/active])
                                   (tr [:status/inactive]))}]}))
            agents)}])
