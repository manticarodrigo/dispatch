(ns ui.views.landing
  (:require ["@faker-js/faker" :refer (faker)]
            [ui.utils.date :as d]
            [ui.components.icons.dispatch
             :refer (dispatch-text)
             :rename {dispatch dispatch-icon}]
            [ui.components.icons.ambito :rename {ambito ambito-icon}]
            [ui.components.parts.stop :refer (stop-order stop-details)]))

(defn landing-view []
  [:div {:class "w-full h-full"}
   [:div {:class "p-6 flex items-center"}
    [dispatch-icon]
    [dispatch-text {:class "ml-2 -mb-1 h-6 w-28"}]]
   [:div {:class "relative w-full h-full"}
    [ambito-icon {:class "absolute p-6 text-amber-50 text-opacity-[0.025] w-full h-full"}]
    [:div {:class "relative p-6 w-full h-screen bg-gradient-to-b from-transparent to-slate-900/50"}
     [:div {:class "flex flex-col lg:flex-row lg:justify-between max-w-7xl mx-auto h-full"}
      [:div {:class "py-8"}
       [:div {:class "mb-8"}
        [:h1 {:class "mb-2 text-4xl font-bold"}
         "Optimize your"
         [:br]
         "spend on travel"]
        [:p {:class "text-lg text-neutral-400"}
         "Measure and increase your revenue"
         [:br]
         "while decreasing your cost per kilometer traveled."]]
       [:div {:class "flex flex-col items-start"}
        [:div {:class "relative group"}
         [:div {:class "absolute -inset-0.5 rounded-lg bg-gradient-to-r from-green-600 to-purple-600 blur opacity-75 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 animate-tilt"}]
         [:a {:href "https://dispatch.ambito.app/register"
              :class "relative block rounded-lg border-2 border-zinc-900/50 py-3 px-5 bg-zinc-900 bg-clip-padding"}
          "Get started for free"]]
        [:span {:class "text-sm text-neutral-300 mt-4"} "First 100 monthly optimized visits are on us."]]]
      [:div {:class "relative row-start-1 col-start-6 xl:col-start-7 col-span-7 xl:col-span-6"}
       [:div {:class "-mx-4 sm:mx-0"}
        [:div {:class "relative border border-neutral-700 rounded-xl bg-neutral-900/70 backdrop-blur shadow-xl"}
         [:div {:class "relative w-full flex flex-col"}
          [:div {:class "flex-none border-b border-neutral-700"}
           [:div {:class "flex items-center h-8 space-x-1.5 px-3"}
            [:div {:class "w-2.5 h-2.5 bg-neutral-600 rounded-full"}]
            [:div {:class "w-2.5 h-2.5 bg-neutral-600 rounded-full"}]
            [:div {:class "w-2.5 h-2.5 bg-neutral-600 rounded-full"}]]]
          [:div {:class "relative min-h-0 flex-auto flex flex-col"}
           [:div {:class "w-full flex-auto flex min-h-0 max-h-[70vh] overflow-auto"}
            [:div {:class "w-full min-w-[33rem] relative flex-auto"}
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
                      :weight (-> (rand (if first? 100 10)) js/parseInt)
                      :volume (-> (rand (if first? 50 5)) js/parseInt)
                      :arrivedAt (when first? end-at)
                      :start-at start-at
                      :end-at end-at}]
                    [:div {:class "mb-2 text-sm"} (.. faker -company name)]
                    [:div {:class "mb-2 font-light text-xs text-neutral-400"} (.. faker -address (streetAddress true))]]])))]]]]]]]]]]])
