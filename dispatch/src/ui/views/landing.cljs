(ns ui.views.landing
  (:require ["@faker-js/faker" :refer (faker)]
            [ui.lib.router :refer (link)]
            [ui.utils.date :as d]
            [ui.utils.string :refer (class-names)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.button :refer (button-class)]
            [ui.components.icons.dispatch
             :refer (dispatch-text)
             :rename {dispatch dispatch-icon}]
            [ui.components.parts.stop :refer (stop-order stop-details)]
            [ui.components.browser :refer (browser)]
            [ui.components.tables.route :refer (route-table)]
            [ui.components.visualizations.world-map :refer (world-map)]
            [ui.components.visualizations.area-chart :refer (area-chart)]
            [ui.components.forms.shipment :refer (shipment-form)]))

(defn landing-header []
  [:header {:class "p-6 flex justify-between items-center"}
   [:div {:class "flex items-center"}
    [dispatch-icon]
    [dispatch-text {:class "ml-2 -mb-1 h-6 w-28"}]
    [:span {:class "sr-only"} "Dispatch"]]
   [:div
    [link {:to "/login" :class (class-names "ml-2" button-class)}
     (tr [:view.login/title])]
    [link {:to "/register" :class (class-names "ml-2" button-class)}
     (tr [:view.register/title])]]])

(defn landing-intro []
  [:section {:class "relative"}
   [world-map {:class "absolute opacity-10"}]
   [:div {:class "relative p-6 w-full lg:min-h-screen bg-gradient-to-b from-neutral-900 via-transparent to-slate-900"}
    [:div {:class "flex flex-col lg:flex-row lg:justify-between max-w-7xl mx-auto"}
     [:div {:class "py-8"}
      [:div {:class "pb-12"}
       [:h1 {:class "pb-2 text-5xl font-bold"}
        (tr [:view.landing.intro/title])]
       [:p {:class "text-xl text-neutral-400"}
        (tr [:view.landing.intro/subtitle])]]
      [:div {:class "flex flex-col items-start"}
       [:div {:class "relative group"}
        [:div {:class "absolute -inset-0.5 rounded-lg bg-gradient-to-r from-teal-600 to-violet-600 blur opacity-75 group-hover:opacity-100 transition duration-1000 group-hover:duration-200 animate-tilt"}]
        [link {:to "/register"
               :class "relative block rounded-lg border-4 border-zinc-900/50 py-3 px-5 bg-zinc-900 bg-clip-padding"}
         (tr [:view.landing.intro/cta])]]
       [:span {:class "text-sm text-neutral-300 pt-4"} "* " (tr [:view.landing.intro/cta-note])]]]
     [browser
      [:div {:class "flex flex-col w-full lg:w-[40rem] h-[20rem] lg:h-[35rem] max-h-full relative flex-auto"}
       [:div {:class "p-4"}
        [:div {:class "text-lg font-medium"} (tr [:view.analytics.charts.revenue-per-gas-liter/title])]
        [:div {:class "text-neutral-400 font-light"} (tr [:view.analytics.charts.revenue-per-gas-liter/subtitle])]]
       [:div {:class "px-2 w-full h-full min-h-0"}
        [area-chart {:format-x (fn [dollars]
                                 (str "$" dollars))
                     :data (map
                            (fn [idx]
                              {:x (d/subDays (js/Date.) (- 29 idx))
                               :y (+ 50 (rand-int 50))})
                            (range 0 30))}]]]]]]])

(defn landing-ai []
  [:section {:class "relative p-6 w-full lg:min-h-[50vh] bg-gradient-to-b from-slate-900 to-transparent"}
   [:div {:class "pb-8 flex flex-col max-w-7xl mx-auto"}
    [:div {:class "py-8 text-center"}
     [:h1 {:class "text-4xl font-bold"}
      (tr [:view.landing.optimization/title])]
     [:p {:class "text-xl text-neutral-400"} (tr [:view.landing.optimization/subtitle])]]
    [browser
     [:div {:class "w-full overflow-auto"}
      [route-table {:agents (map (fn [idx]
                                   {:id idx
                                    :name (.. faker -name fullName)})
                                 (range 5))
                    :result (map (fn [idx]
                                   {:vehicle {:id idx
                                              :name (str (.. faker -vehicle vrm) " " (rand-int 8) "L")
                                              :volume 100
                                              :weight 100}
                                    :start (d/startOfDay (js/Date.))
                                    :end (d/endOfDay (js/Date.))
                                    :meters (+ 99999 (rand 500000))
                                    :volume (+ 80 (rand-int 20))
                                    :weight (+ 80 (rand-int 20))
                                    :visits (apply array
                                                   (map
                                                    (fn [idx]
                                                      {:arrival (-> (js/Date.) d/startOfDay (d/addHours idx))
                                                       :shipment {}})
                                                    (range 10)))})
                                 (range 5))
                    :selected-rows #js[]
                    :set-selected-rows #()
                    :selected-agents (into [] (range 5))}]]]]])

(defn landing-constraints []
  [:section {:class "relative p-6 w-full lg:min-h-screen bg-gradient-to-b from-transparent via-transparent to-teal-950"}
   [:div {:class "py-8 flex flex-col lg:flex-row lg:justify-between max-w-7xl mx-auto"}
    [:div {:class "py-8"}
     [:h1 {:class "pb-2 text-4xl font-bold"}
      (tr [:view.landing.constraints/title])]
     [:p {:class "text-xl text-neutral-400"}
      (tr [:view.landing.constraints/subtitle])]]
    [browser
     [:div {:class "p-4"}
      [shipment-form]]]]])

(defn landing-task []
  [:section {:class "relative p-6 w-full lg:min-h-screen bg-gradient-to-b from-teal-950 via-violet-950 to-purple-950"}
   [:div {:class "flex flex-col lg:flex-row lg:justify-between max-w-7xl mx-auto"}
    [:div {:class "py-8"}
     [:div {:class "pb-8"}
      [:h1 {:class "pb-2 text-4xl font-bold"}
       (tr [:view.landing.monitoring/title])]
      [:p {:class "text-lg text-neutral-400"}
       (tr [:view.landing.monitoring/subtitle])]]]
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
             [:div {:class "mb-2 font-light text-xs text-neutral-400"} (.. faker -address (streetAddress true))]]])))]]]])

(defn landing-view []
  [:<>
   [landing-header]
   [landing-intro]
   [landing-ai]
   [landing-constraints]
   [landing-task]])
