(ns ui.components.lists.stop
  (:require ["react-feather" :rename {Navigation NavigationIcon
                                      Flag DoneIcon
                                      Clock BreakIcon}]
            [clojure.string :as s]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.button :refer (button button-class)]))

(defn merge-stops [stops route]
  (let [{:keys [visits transitions travelSteps]} route
        mergeable-count (if (seq transitions)
                          (count transitions)
                          (count stops))]
    (reduce
     (fn [acc idx]
       (let [visit (get visits idx)
             transition (get transitions (inc idx))
             travel-step (get travelSteps (inc idx))]
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
        (let [last? (= idx (dec (count merged-stops)))
              {:keys [name description lat lng]} place
              pickup? (->> visits (some :isPickup))
              delivery? (and (seq visits)
                             (not-any? :isPickup visits))
              transition-start-at (some-> transitions last :startTime js/Date.)
              prev-stop (get merged-stops (dec idx))
              prev-stop-duration (some-> prev-stop :travel-steps last :duration js/parseInt)
              prev-stop-arrived-at (some-> prev-stop :arrivedAt)
              active? (and (not arrivedAt)
                           (or (nil? prev-stop) prev-stop-arrived-at))
              prev-transition-start-at (when prev-stop (some-> prev-stop :transitions last :startTime js/Date.))
              prev-transition-end-at (when prev-transition-start-at (d/addSeconds prev-transition-start-at prev-stop-duration))
              next-leg (when-not last? (get (:legs route) (inc idx)))
              transition-kms (or (some-> travel-steps last :distanceMeters js/parseInt (/ 1000) js/Math.round)
                                 (some-> next-leg :distance (/ 1000) js/Math.round))
              transition-mins (or (some-> travel-steps last :duration js/parseInt (/ 60) js/Math.round)
                                  (some-> next-leg :duration (/ 60) js/Math.round))
              transition-loads (some-> transitions last :vehicleLoads)
              transition-break-minutes (some-> transitions last :breakDuration js/parseInt (/ 60) int)
              transition-kg (or (some-> transition-loads :weight :amount js/parseInt (/ 1000000) int) 0)
              transition-m3 (or (some-> transition-loads :volume :amount js/parseInt (/ 1000000) int) 0)
              start-at (or
                        (some-> visits first :startTime js/Date.)
                        prev-transition-end-at
                        (d/addSeconds startAt (reduce + (map :duration (take (inc idx) (:legs route))))))
              end-at (or transition-start-at (and shipment (d/addSeconds start-at (or (-> shipment :duration) 0))))]
          ^{:key id}
          [:li {:class (class-names
                        "relative border-b border-neutral-800 w-full"
                        (when-not active? "opacity-50"))}
           (when-not last?
             [:div {:class "absolute top-full -translate-y-1/2 w-full text-center"}
              (when transition-break-minutes
                [:div {:class (class-names
                               "inline mx-2"
                               "rounded py-1 px-2"
                               "text-xs text-slate-200 bg-slate-800")}
                 [:> BreakIcon {:class "inline mr-2 w-3 h-3"}]
                 transition-break-minutes " mins break"])
              [:div {:class (class-names
                             "inline mx-2"
                             "rounded py-1 px-2"
                             "text-xs text-zinc-200 bg-zinc-800")}
               [:> NavigationIcon {:class "inline mr-2 w-3 h-3"}]
               transition-kms " km"
               " "
               transition-mins " mins"]])
           [:div {:class "flex p-4"}
            [:div {:class "relative font-bold text-sm"}
             [:div {:class "absolute top-6 left-1/2 -translate-x-1/2 border-l-2 border-dashed border-neutral-800 h-[calc(100%_-_1rem)]"}]
             (when (< (inc idx) 10) "0")
             (inc idx)]
            [:div {:class "pl-4 w-full min-w-0"}
             [:div {:class "flex justify-between"}
              [:div {:class "mb-2 flex flex-col items-start"}
               (cond
                 pickup? [:div {:class "mr-2 flex items-center rounded-full py-1 px-2 text-xs font-medium text-blue-50 bg-blue-800"}
                          [:div {:class "mr-1 rounded-full h-2 w-2 bg-blue-300"}]
                          "Pickup"]
                 delivery? [:div {:class "mr-2 flex items-center rounded-full py-1 px-2 text-xs font-medium text-green-50 bg-green-800"}
                            [:div {:class "mr-1 rounded-full h-2 w-2 bg-green-300"}]
                            "Delivery"]
                 :else [:div {:class "mr-2 flex items-center rounded-full py-1 px-2 text-xs font-medium text-neutral-50 bg-neutral-800"}
                        [:div {:class "mr-1 rounded-full h-2 w-2 bg-neutral-300"}]
                        "Stop"])
               (when (seq visits)
                 [:div {:class "mt-1 text-xs text-neutral-400"}
                  (count visits) " orders"
                  " • "
                  transition-kg " kg"
                  " • "
                  transition-m3 " m³"])]
              [:div {:class "shrink-0 flex flex-col justify-between items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
               [:div
                (if arrivedAt
                  (s/capitalize (d/formatRelative arrivedAt (js/Date.)))
                  [:div {:class "flex"}
                   (when start-at (d/format start-at "hh:mm aaa"))
                   (when (and start-at end-at) " - ")
                   (when end-at (d/format end-at "hh:mm aaa"))])
                [:div {:class "font-medium text-xs text-neutral-500 uppercase"}
                 (cond
                   arrivedAt [:span {:class "text-green-500"} "Completed"]
                   :else "Scheduled")]]]]
             [:div {:class "mb-2  text-sm"} name]
             [:div {:class "mb-2 font-light text-xs text-neutral-400"} description]
             (when active?
               [:div {:class "grid grid-cols-2 gap-2 my-2"}
                [:a {:target "_blank"
                     :href (str "https://www.google.com/maps/dir/?api=1&origin=Current+Location&destination=" lat "," lng)
                     :class (class-names button-class "text-center")}
                 [:div
                  [:> NavigationIcon {:class "inline mr-2 w-3 h-3"}]
                  "Navigate"]]
                [button {:label [:div
                                 [:> DoneIcon {:class "inline mr-2 w-3 h-3"}]
                                 "Finish"]}]])]]])))
     (if loading
       [:p {:class "p-4 text-center"} (tr [:misc/loading]) "..."]
       (when (empty? stops)
         [:p {:class "p-4 text-center"} (tr [:misc/empty-search])]))]))
