(ns ui.views.seat.task.detail
  (:require [react]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.subs :refer (listen)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.css :refer (padding)]
            [ui.components.title :refer (title)]
            [ui.components.lists.stop :refer (stop-list)]))

(def FETCH_TASK (gql (inline "queries/task/fetch-by-device.graphql")))

(defn view []
  (let [{seat-id :seat task-id :task} (use-params)
        device (listen [:device])
        device-id (:id device)
        query (use-query FETCH_TASK {:variables {:taskId task-id
                                                 :seatId seat-id
                                                 :deviceId device-id}})
        {:keys [data loading]} query
        {:keys [seat stops route]} (:task data)
        {:keys [path]} route
        {:keys [name location]} seat
        location-date (some-> location :createdAt js/parseInt js/Date.)]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:paths (when path [path])
                   :points (->> stops
                                (mapv :place)
                                (mapv (fn [{:keys [lat lng name]}]
                                        {:title name
                                         :position {:lat lat :lng lng}})))}])
       #())
     #js[route stops])

    [:div {:class padding}
     [title {:title name :subtitle (tr [:status/last-seen] [location-date])}]
     [stop-list {:stops stops :loading loading}]]))
