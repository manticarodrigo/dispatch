(ns ui.views.organization.agent.performance
  (:require ["react" :refer (useState useEffect)]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [common.utils.promise :refer (each)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.google.maps.directions :refer (calc-route calc-optimized-route)]
            [ui.utils.input :refer (debounce-cb)]
            [ui.utils.color :refer (get-color)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.inputs.date :refer (date-select)]
            [ui.views.organization.agent.performance :as performance]))

(def FETCH_ORGANIZATION_AGENT_PERFORMANCE (gql (inline "queries/user/organization/fetch-agent-performance.graphql")))

(defn connected-partition [n coll]
  (when (seq coll)
    (lazy-seq (cons (take n coll) (connected-partition n (drop (dec n) coll))))))

(defn partitioned-calcs [calc-fn places]
  (let [partitions (connected-partition 25 places)
        route-fns (map (fn [partition] #(calc-fn partition)) partitions)]
    (-> (each route-fns)
        (.then (fn [routes]
                 (reduce (fn [acc route]
                           {:legs (concat (:legs acc) (:legs route))
                            :path (concat (:path acc) (:path route))})
                         {}
                         routes))))))

(defn view []
  (let [[selected-date set-selected-date] (useState (js/Date. "2023-02-28"))
        query (use-query FETCH_ORGANIZATION_AGENT_PERFORMANCE {:variables {:start (d/startOfDay selected-date)
                                                                           :end (d/endOfDay selected-date)}})
        {:keys [data loading]} query
        {:keys [performance]} (some-> data :user :organization)
        [routes set-routes] (useState [])
        [optimized-routes set-optimized-routes] (useState nil)
        agents (->> performance
                    (filterv #(> (count (:places %)) 2)))]

    (useEffect
     (fn []
       (if (and (not loading)
                (seq agents))
         (let [places-groups (->> agents (mapv :places))
               routes-fns (mapv
                           (fn [places]
                             (fn []
                               (if (seq places)
                                 (partitioned-calcs calc-route places)
                                 (js/Promise.resolve))))
                           places-groups)
               optimized-routes-fns (mapv
                                     (fn [places]
                                       (fn []
                                         (if (seq places)
                                           (partitioned-calcs calc-optimized-route places)
                                           (js/Promise.resolve))))
                                     places-groups)]
           (-> (each routes-fns)
               (.then set-routes))
           (-> (each optimized-routes-fns)
               (.then set-optimized-routes)))
         (do (set-routes nil)
             (set-optimized-routes nil)))
       #())
     (array loading))

    (use-map-items
     loading
     {:tasks (when routes (mapv (fn [route] (when route {:route route})) (concat routes optimized-routes)))
      :places (->> agents
                   (map-indexed (fn [idx {:keys [places]}]
                                  (map (fn [{:keys [_ lat lng]}]
                                         {:name " "
                                          :color (get-color idx)
                                          :lat lat
                                          :lng lng})
                                       places)))
                   flatten)}
     [performance routes])

    [map-layout {:title (if loading (str (tr [:misc/loading]) "...") (or (some-> selected-date (d/format "yyyy-MM-dd")) (tr [:field/date])))}
     [:div {:class "p-4 w-full h-full overflow-auto"}
      [date-select
       {:label (tr [:field/date])
        :placeholder (tr [:field/date])
        :value selected-date
        :required true
        :class "mb-4"
        :on-select set-selected-date}]
      (doall
       (for [[idx {:keys [id name]}] (->> agents (map-indexed vector))]
         (let [route (get routes idx)
               optimized-route (get optimized-routes idx)
               route-total-travel-distance (->> route :legs (map :distance) (reduce +))
               route-total-travel-time (->> route :legs (map :duration) (reduce +))
               route-total-km (-> route-total-travel-distance (/ 1000) (.toFixed 2))
               route-total-min (-> route-total-travel-time (/ 60) (.toFixed 2))
               optimized-route-total-travel-distance (->> optimized-route :legs (map :distance) (reduce +))
               optimized-route-total-travel-time (->> optimized-route :legs (map :duration) (reduce +))
               optimized-route-total-km (-> optimized-route-total-travel-distance (/ 1000) (.toFixed 2))
               optimized-route-total-min (-> optimized-route-total-travel-time (/ 60) (.toFixed 2))
               difference-km (-> (- route-total-travel-distance optimized-route-total-travel-distance) (/ 1000) (.toFixed 2))
               difference-min (-> (- route-total-travel-time optimized-route-total-travel-time) (/ 60) (.toFixed 2))]
           ^{:key id}
           [:div {:class "mb-4"}
            [:h3 {:class "text-sm font-bold mb-2"} name]
            (when route
              [:div {:class "grid grid-cols-2 gap-4"}
               [:div {:class "mb-2"}
                [:div {:class "text-neutral-400 text-xs"}
                 (tr [:field/total-travel-distance "Actual travel distance"])]
                [:div {:class "text-sm"}
                 route-total-km " km"]]
               [:div {:class "mb-2"}
                [:div {:class "text-neutral-400 text-xs"}
                 (tr [:field/total-travel-time "Actual travel time"])]
                [:div  {:class "text-sm"}
                 route-total-min " min"]]
               [:div {:class "mb-2"}
                [:div {:class "text-neutral-400 text-xs"}
                 (tr [:field/total-travel-distance-optimized "Optimized travel distance"])]
                [:div {:class "text-sm"}
                 optimized-route-total-km " km"]]
               [:div {:class "mb-2"}
                [:div {:class "text-neutral-400 text-xs"}
                 (tr [:field/total-travel-time-optimized "Optimized travel time"])]
                [:div  {:class "text-sm"}
                 optimized-route-total-min " min"]]
               [:div {:class "mb-2"}
                [:div {:class "text-neutral-400 text-xs"}
                 (tr [:field/total-travel-distance-difference "Travel distance difference"])]
                [:div  {:class "text-sm"}
                 difference-km " km"]]
               [:div {:class "mb-2"}
                [:div {:class "text-neutral-400 text-xs"}
                 (tr [:field/total-travel-time-difference "Travel time difference"])]
                [:div  {:class "text-sm"}
                 difference-min " min"]]])])))]]))
