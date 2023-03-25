(ns ui.components.lists.stop
  (:require ["react-feather" :rename {Check CheckIcon
                                      Minus MinusIcon
                                      Package PackageIcon
                                      Clock ClockIcon}]
            [ui.utils.date :as d]
            [ui.components.lists.link-list :refer (link-list)]))

(defn add-cumulative-durations [stops route]
  (let [{:keys [legs visits]} route
        merged-visits (reduce
                       (fn [acc [idx visit]]
                         (if (and (seq acc)
                                  (:isPickup visit)
                                  (:isPickup (nth visits (dec idx))))
                           (conj (pop acc) (conj (last acc) visit))
                           (conj acc [visit])))
                       []
                       (map-indexed vector visits))]
    (prn (count stops) (count merged-visits))

    (map-indexed
     (fn [idx stop]
       (merge stop {:leg (nth legs idx)
                    :visits (nth merged-visits idx)}))
     stops)))


(defn stop-list [{:keys [task loading]}]
  (let [{:keys [stops route]} task]
    [link-list
     {:type :ol
      :loading loading
      :items (map-indexed
              (fn [idx {:keys [id place arrivedAt visits shipment]}]
                (let [{:keys [name description]} place
                      start-at (-> visits
                                   first
                                   :startTime
                                   js/Date.)
                      end-at (d/addSeconds start-at (or (-> shipment :duration) 0))]
                  (prn end-at)
                  {:id id
                   :to (str "../stops/" id)
                   :decorator [:span {:class "text-sm"}
                               (when (< (inc idx) 10) "0")
                               (inc idx)]
                   :title name
                   :subtitle description
                   :detail [:div {:class "flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
                            (if arrivedAt
                              [:div {:class "flex"}
                               [:> PackageIcon {:class "mr-3 w-4 h-4 text-green-500"}]
                               (d/format arrivedAt "hh:mmaaa")]
                              [:div {:class "flex"}
                               [:> ClockIcon {:class "mr-3 w-4 h-4 text-neutral-500"}]
                               (d/format start-at "hh:mmaaa") "-" (d/format end-at "hh:mmaaa")])]}))
              stops)}]))
