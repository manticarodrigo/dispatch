(ns ui.components.lists.stop
  (:require ["react-feather" :rename {Navigation NavigationIcon
                                      Flag DoneIcon}]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.popover :refer (popover)]
            [ui.components.inputs.button :refer (button button-class)]
            [ui.components.parts.stop :refer (stop-order stop-transition stop-details)]
            [ui.components.forms.stop :refer (stop-form)]))

(defn merge-stops [stops route]
  (let [{:keys [visits transitions travelSteps]} route
        mergeable-count (if (seq transitions)
                          (count transitions)
                          (count stops))]
    (reduce
     (fn [acc idx]
       (let [visit (get visits idx)
             transition (get transitions (inc idx))
             travel-step (get travelSteps idx)]
         (if (and (seq acc)
                  (:isPickup visit)
                  (:isPickup (get visits (dec idx))))
           (conj (pop acc) (merge (last acc) {:transitions (conj (:transitions (last acc)) transition)
                                              :travel-steps (conj (:travel-steps (last acc)) travel-step)
                                              :visits (conj (:visits (last acc)) visit)}))
           (conj acc (merge
                      (get stops (count acc))
                      {:transitions (when transition [transition])
                       :travel-steps (when travel-step [travel-step])
                       :visits (when visit [visit])})))))
     []
     (range mergeable-count))))

(defn stop-list [{:keys [task loading]}]
  (let [{:keys [startAt stops route]} task
        merged-stops (merge-stops stops route)]
    [:ol {:class "overflow-auto"}
     (doall
      (for [[idx {:keys [id place arrivedAt shipment visits transitions travel-steps]}] (map-indexed vector merged-stops)]
        (let [{:keys [name description lat lng]} place
              pickup? (->> visits (some :isPickup))
              delivery? (and (seq visits)
                             (not-any? :isPickup visits))
              leg (get (:legs route) idx)
              leg-distance (or (some-> travel-steps last :distanceMeters js/parseInt)
                               (some-> leg :distance))
              leg-duration (or (some-> travel-steps last :duration js/parseInt)
                               (some-> leg :duration))
              prev-stop (get merged-stops (dec idx))
              prev-stop-arrived-at (some-> prev-stop :arrivedAt)
              active? (and (not arrivedAt)
                           (or (nil? prev-stop) prev-stop-arrived-at))
              prev-transition-start-at (when prev-stop (some-> prev-stop :transitions last :startTime js/Date.))
              prev-transition-end-at (when prev-transition-start-at (d/addSeconds prev-transition-start-at leg-duration))
              prev-transition-break-duration (some-> prev-stop :transitions last :breakDuration)

              transition-loads (some-> transitions last :vehicleLoads)
              transition-kg (or (some-> transition-loads :weight :amount js/parseInt (/ 1000000) int) 0)
              transition-m3 (or (some-> transition-loads :volume :amount js/parseInt (/ 1000000) int) 0)
              transition-start-at (some-> transitions last :startTime js/Date.)
              start-at (or
                        (some-> visits first :startTime js/Date.)
                        prev-transition-end-at
                        (d/addSeconds startAt (reduce + (map :duration (take (inc idx) (:legs route))))))
              end-at (or transition-start-at (and shipment (d/addSeconds start-at (or (-> shipment :duration) 0))))]
          ^{:key id}
          [:li {:class (class-names
                        "relative divide-y divide-neutral-800 w-full"
                        (when-not (or arrivedAt active?) "opacity-50"))}
           (when (> idx 0)
             [:div {:class "absolute bottom-full translate-y-1/2 w-full text-center"}
              [stop-transition
               {:break prev-transition-break-duration
                :duration leg-duration
                :distance leg-distance}]])
           [:div {:class "flex py-6 px-4"}
            [stop-order idx]
            [:div {:class "pr-2 pl-4 w-full min-w-0"}
             [stop-details
              {:type (cond
                       pickup? :pickup
                       delivery? :delivery
                       :else nil)
               :visits visits
               :weight transition-kg
               :volume transition-m3
               :arrivedAt arrivedAt
               :start-at start-at
               :end-at end-at}]
             [:div {:class "mb-2 text-sm"} name]
             [:div {:class "mb-2 font-light text-xs text-neutral-400"} description]
             (when active?
               [:div {:class "grid grid-cols-2 gap-2 mt-4 mb-2"}
                [:a {:target "_blank"
                     :href (str "https://www.google.com/maps/dir/?api=1&origin=Current+Location&destination=" lat "," lng)
                     :class (class-names button-class "text-center")}
                 [:div
                  [:> NavigationIcon {:class "inline mr-2 w-3 h-3"}]
                  "Navigate"]]

                [popover
                 [button {:class (class-names
                                  "w-full"
                                  "!border-blue-700 focus:!border-blue-500 hover:!border-blue-500 active:!border-blue-500"
                                  "!bg-blue-800 focus:!bg-blue-700 hover:!bg-blue-700 active:!bg-blue-700")
                          :label [:div
                                  [:> DoneIcon {:class "inline mr-2 w-3 h-3"}]
                                  "Finish"]}]
                 [:div {:class "p-4"}
                  [stop-form {:id id}]]]])]]])))
     (if loading
       [:p {:class "p-4 text-center"} (tr [:misc/loading]) "..."]
       (when (empty? stops)
         [:p {:class "p-4 text-center"} (tr [:misc/empty-search])]))]))
