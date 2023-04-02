(ns ui.views.landing
  (:require ["@faker-js/faker" :refer (faker)]
            [ui.utils.date :as d]
            [ui.components.icons.dispatch
             :refer (dispatch-text)
             :rename {dispatch dispatch-icon}]
            [ui.components.parts.stop :refer (stop-order stop-details)]
            [ui.components.browser :refer (browser)]
            [ui.components.visualizations.world-map :refer (world-map)]
            [ui.components.visualizations.area-chart :refer (area-chart)]))

(defn landing-view []
  [:div {:class "w-full h-full"}
   [:div {:class "p-6 flex items-center"}
    [dispatch-icon]
    [dispatch-text {:class "ml-2 -mb-1 h-6 w-28"}]]
   [:div {:class "relative w-full min-h-full"}
    [world-map {:class "absolute opacity-10"}]
    [:div {:class "relative p-6 w-full h-screen bg-gradient-to-b from-neutral-900 via-slate-900/10 to-slate-900"}
     [:div {:class "flex flex-col lg:flex-row lg:justify-between max-w-7xl mx-auto h-full"}
      [:div {:class "py-8"}
       [:div {:class "pb-12"}
        [:h1 {:class "pb-2 text-5xl font-bold"}
         "Optimize your"
         [:br]
         "spend on travel"]
        [:p {:class "text-xl text-neutral-400"}
         "Measure and increase your revenue"
         [:br]
         "while decreasing your cost per kilometer traveled."]]
       [:div {:class "flex flex-col items-start"}
        [:div {:class "relative group"}
         [:div {:class "absolute -inset-0.5 rounded-lg bg-gradient-to-r from-teal-600 to-violet-600 blur opacity-75 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 animate-tilt"}]
         [:a {:href "https://dispatch.ambito.app/register"
              :class "relative block rounded-lg border-4 border-zinc-900/50 py-3 px-5 bg-zinc-900 bg-clip-padding"}
          "Get started for free"]]
        [:span {:class "text-sm text-neutral-300 pt-4"} "* First 100 monthly optimized visits are on us."]]]
      [browser
       [:div {:class "flex flex-col w-full lg:w-[40rem] h-[20rem] lg:h-[35rem] relative flex-auto"}
        [:div {:class "p-4"}
         [:div {:class "text-lg font-medium"} "Daily revenue"]
         [:div {:class "text-neutral-400 font-light"} "Last 30 days"]]
        [:div {:class "px-2 w-full h-full min-h-0"}
         [area-chart {:format-x (fn [dollars]
                                  (str "$" (if (> dollars 999)
                                             (str (int (/ dollars 1000)) "k")
                                             dollars)))
                      :data (map
                             (fn [idx]
                               {:x (d/subDays (js/Date.) (- 29 idx))
                                :y (max 5000 (rand-int 10000))})
                             (range 0 30))}]]]]]]]
   [:div {:class "relative p-6 w-full h-screen bg-gradient-to-b from-slate-900 to-transparent"}
    [:div {:class "flex flex-col lg:flex-row lg:justify-between max-w-7xl mx-auto h-full"}
     [:div {:class "py-8"}
      [:div {:class "pb-8"}
       [:h1 {:class "pb-2 text-4xl font-bold"}
        "Solve problems on the road"
        [:br]
        "with real-time feedback"]
       [:p {:class "text-lg text-neutral-400"}
        "Detect issues and update"
        [:br]
        "stop sequences, constraints, and statuses."]]]
     [browser
      [:div {:class "w-full lg:min-w-[33rem] relative flex-auto overflow-auto"}
       (doall
        (for [idx (range 5)]
          (let [first? (= idx 0)
                start-at (-> (js/Date.) d/startOfDay (d/addHours idx))
                end-at (-> start-at (d/addMinutes 30))]
            ^{:key idx}
            [:div {:class "flex py-6 px-4"}
             [stop-order idx]
             [:div {:class "pr-2 pl-4 w-full min-w-0"}
              [stop-details
               {:type (cond
                        first? :pickup
                        :else :delivery)
                :visits (if first? (range 5) [1])
                :weight (rand-int (if first? 100 10))
                :volume (rand-int (if first? 50 5))
                :arrivedAt (when first? end-at)
                :start-at start-at
                :end-at end-at}]
              [:div {:class "mb-2 text-sm"} (.. faker -company name)]
              [:div {:class "mb-2 font-light text-xs text-neutral-400"} (.. faker -address (streetAddress true))]]])))]]]]])
