(ns ui.components.lists.stop
  (:require [react-feather :rename {Check CheckIcon
                                    Minus MinusIcon
                                    Package PackageIcon
                                    Clock ClockIcon}]
            [ui.utils.date :as d]
            [ui.components.lists.link-list :refer (link-list)]))

(defn add-cumulative-durations [stops legs]
  (let [durations (map :duration legs)]
    (map-indexed
     (fn [idx stop]
       (assoc stop :cumulative-duration (reduce + (take (inc idx) durations))))
     stops)))

(defn stop-list [{:keys [task loading]}]
  (let [{:keys [stops route startAt]} task
        stops-with-duration (add-cumulative-durations stops (:legs route))]
    [link-list
     {:type :ol
      :loading loading
      :items (map
              (fn [{:keys [id place arrivedAt cumulative-duration]}]
                (let [{:keys [name description]} place]
                  {:id id
                   :to (str "../stops/" id)
                   :icon (if arrivedAt CheckIcon MinusIcon)
                   :title name
                   :subtitle description
                   :detail [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
                            (if arrivedAt
                              [:div {:class "flex"}
                               [:> PackageIcon {:class "mr-3 w-4 h-4 text-green-500"}]
                               (d/format arrivedAt "hh:mmaaa")]
                              [:div {:class "flex"}
                               [:> ClockIcon {:class "mr-3 w-4 h-4 text-neutral-500"}]
                               (-> startAt
                                   (d/addSeconds cumulative-duration)
                                   (d/format "hh:mmaaa"))])]}))
              stops-with-duration)}]))
