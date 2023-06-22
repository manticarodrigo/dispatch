(ns ui.views.organization.agent.locations
  (:require ["react" :refer (useState useEffect)]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params)]
            [ui.lib.google.maps.directions :refer (calc-route calc-optimized-route)]
            [ui.utils.input :refer (debounce-cb)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.inputs.combobox :refer (combobox)]))

(def FETCH_ORGANIZATION_AGENT_LOCATIONS (gql (inline "queries/user/organization/fetch-agent-locations.graphql")))

(defn connected-partition [n coll]
  (when (seq coll)
    (lazy-seq (cons (take n coll) (connected-partition n (drop (dec n) coll))))))

(defn view []
  (let [{agent-id :agent} (use-params)
        query (use-query FETCH_ORGANIZATION_AGENT_LOCATIONS {:variables {:agentId agent-id}})
        {:keys [data loading]} query
        {:keys [name locations places]} (some-> data :user :organization :agent)
        grouped-locations (->> (group-by
                                (fn [location] (-> location :createdAt parse-date (d/format "yyyy-MM-dd")))
                                locations)
                               (sort-by (fn [[date _]]
                                          (js/Date. date)) <))
        [routes set-routes] (useState nil)
        [optimized-routes set-optimized-routes] (useState nil)
        [selected-date set-selected-date] (useState nil)
        [current-location-index set-current-location-index] (useState 0)
        selected-locations (->> grouped-locations
                                (filter (fn [[date _]] (= date selected-date)))
                                (map second)
                                first)
        current-location (nth selected-locations current-location-index)
        current-date-created-places (filter
                                     (fn [place] (= (-> place :createdAt parse-date (d/format "yyyy-MM-dd"))
                                                    selected-date))
                                     places)
        routes-total-travel-distance (->> routes (map #(->> % :legs (map :distance) (reduce +))) (reduce +))
        routes-total-travel-duration (->> routes (map #(->> % :legs (map :duration) (reduce +))) (reduce +))
        optimized-total-travel-distance (->> optimized-routes (map #(->> % :legs (map :distance) (reduce +))) (reduce +))
        optimized-total-travel-duration (->> optimized-routes (map #(->> % :legs (map :duration) (reduce +))) (reduce +))]

    (useEffect
     (fn []
       (let [time-range-interval
             (when selected-date
               (js/setInterval
                (fn []
                  (set-current-location-index
                   (fn [current]
                     (if (= current (dec (count selected-locations)))
                       0
                       (inc current)))))
                50))]
         (fn []
           (js/clearInterval time-range-interval))))
     (array selected-date))

    (useEffect
     (fn []
       (dispatch [:map/locations
                  [{:title (-> current-location :createdAt parse-date (d/format "hh:mm aaa"))
                    :position (:position current-location)}]])
       #())
     (array current-location-index))

    (useEffect
     (fn []
       (debounce-cb
        (fn []
          (if (seq current-date-created-places)
            (let [places-groups (connected-partition 25 current-date-created-places)
                  routes (mapv
                          (fn [places]
                            (calc-route places))
                          places-groups)
                  optimized-routes (mapv
                                    (fn [places]
                                      (calc-optimized-route places))
                                    places-groups)]
              (-> (js/Promise.all routes)
                  (.then set-routes))
              (-> (js/Promise.all optimized-routes)
                  (.then set-optimized-routes)))
            (do (set-routes nil)
                (set-optimized-routes nil)))
          #())
        500)
       #())
     (array selected-date))

    (use-map-items
     loading
     {:tasks (when routes (mapv (fn [route] {:route route}) routes))
      :places current-date-created-places}
     [selected-date routes])

    [map-layout {:title (if loading (str (tr [:misc/loading]) "...") name)}
     [:div {:class "p-4 w-full h-full overflow-auto"}
      [combobox
       {:class "w-full"
        :options grouped-locations
        :value selected-date
        :option-to-label (fn [[date _]] date)
        :option-to-value (fn [[date _]] date)
        :on-change #(do
                      (set-selected-date %)
                      (set-current-location-index 0))}]

      (when (seq routes)
        [:<>
         [:div {:class "flex items-center"}
          [:div {:class "text-sm text-gray-600"}
           (str routes-total-travel-distance " m")]
          [:div {:class "text-sm text-gray-600"}
           (str routes-total-travel-duration " s")]]
         [:div {:class "flex items-center"}
          [:div {:class "text-sm text-gray-600"}
           (str optimized-total-travel-distance " m")]
          [:div {:class "text-sm text-gray-600"}
           (str optimized-total-travel-duration " s")]]])]]))
