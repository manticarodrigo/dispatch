(ns ui.components.parts.stop
  (:require ["react-feather" :rename {Navigation NavigationIcon
                                      Clock BreakIcon}]
            [clojure.string :as s]
            [ui.utils.date :as d]
            [ui.utils.string :refer (class-names)]))

(defn stop-order [idx]
  [:div {:class "relative font-bold text-sm"}
   [:div {:class "absolute top-6 left-1/2 -translate-x-1/2 border-l-2 border-dashed border-neutral-700 h-[calc(100%_-_1rem)]"}]
   (when (< (inc idx) 10) "0")
   (inc idx)])

(defn stop-transition [{:keys [break duration distance]}]
  [:<>
   (when break
     [:div {:class (class-names
                    "inline mx-2"
                    "rounded py-1 px-2"
                    "text-xs text-slate-200 bg-slate-800")}
      [:> BreakIcon {:class "inline mr-2 w-3 h-3"}]
      (-> break js/parseInt (/ 60) int) " mins break"])
   [:div {:class (class-names
                  "inline mx-2"
                  "rounded py-1 px-2"
                  "text-xs text-zinc-200 bg-zinc-800")}
    [:> NavigationIcon {:class "inline mr-2 w-3 h-3"}]
    (d/formatDistanceStrict 0 (* duration 1000))
    ", "
    (-> distance (/ 1000) js/Math.round) " km"]])

(defn stop-details [{:keys [type visits weight volume status finished-at start-at end-at]}]
  [:div {:class "flex justify-between"}
   [:div {:class "mb-2 flex flex-col items-start"}
    (case type
      :pickup [:div {:class "mr-2 flex items-center rounded-full py-1 px-2 text-xs font-medium text-blue-50 bg-blue-800"}
               [:div {:class "mr-1 rounded-full h-2 w-2 bg-blue-300"}]
               "Pickup"]
      :delivery [:div {:class "mr-2 flex items-center rounded-full py-1 px-2 text-xs font-medium text-green-50 bg-green-800"}
                 [:div {:class "mr-1 rounded-full h-2 w-2 bg-green-300"}]
                 "Delivery"]
      [:div {:class "mr-2 flex items-center rounded-full py-1 px-2 text-xs font-medium text-neutral-50 bg-neutral-800"}
       [:div {:class "mr-1 rounded-full h-2 w-2 bg-neutral-300"}]
       "Stop"])
    (when (seq visits)
      [:div {:class "mt-1 text-xs text-neutral-400"}
       (count visits) " orders"
       " • "
       weight " kg"
       " • "
       volume " m³"])]
   [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
    (if finished-at
      (s/capitalize (d/formatRelative finished-at (js/Date.)))
      [:div {:class "flex"}
       (when start-at (d/format start-at "hh:mm aaa"))
       (when (and start-at end-at) " - ")
       (when end-at (d/format end-at "hh:mm aaa"))])
    [:div {:class "font-medium text-xs text-neutral-500 uppercase"}
     (case status
       "COMPLETE" [:span {:class "text-green-500"} "Complete"]
       "INCOMPLETE" [:span {:class "text-yellow-500"} "Incomplete"]
       "Scheduled")]]])
