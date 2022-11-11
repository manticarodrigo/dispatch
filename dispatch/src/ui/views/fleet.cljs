(ns ui.views.fleet
  (:require
   [react-feather :rename {Check CheckIcon
                           Package PackageIcon
                           GitPullRequest DistanceIcon
                           Clock DurationIcon}]
   [ui.subs :refer (listen)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.button :refer (button base-button-class)]
   [ui.components.inputs.generic.accordion :refer (accordion)]))

(defonce distance-str #(tr [:view.fleet/distance]))
(defonce duration-str #(tr [:view.fleet/duration]))

(defn- route-leg-fact [label value]
  [:div {:class "flex justify-center items-center mb-1 text-neutral-300"}
   [:span {:class "sr-only"} label " "]
   value])


(defn- route-leg [idx address distance duration]
  (let [delivered? (< idx 2)]
    [:div {:class (class-names  "py-2 flex")}
     [:div {:class (class-names "relative"
                                "shrink-0 flex justify-center items-center"
                                "rounded-full border border-neutral-300"
                                "w-6 h-6"
                                "font-bold bg-neutral-900")}

      (when delivered? [:> CheckIcon {:class "w-4 h-4"}])]
     [:div {:class "pl-2 lg:pl-6 flex w-full"}
      [:div {:class "w-full"}
       [:p {:class "text-base font-medium"} (str "Waypoint " (+ 1 idx))]
       [:p {:class "text-xs text-neutral-300"} address]]
      [:div {:class "shrink-0 flex flex-col items-end pl-4 lg:pl-6 text-xs text-neutral-300"}
       (if delivered?
         [:div {:class "flex"}
          [:> PackageIcon {:class "mr-1 w-4 h-4"}]
          "09:45pm"]
         [:<>
          [:div {:class "flex"}
           [:> DurationIcon {:class "mr-1 w-4 h-4"}]
           "10:15pm"]])]]]))

(defn driver-detail []
  (let [legs (listen [:route/legs])]
    (when (> (count legs) 0)
      [:section {:class (class-names "p-2.5")}
       [:h2 {:class (class-names "sr-only")}
        (tr [:view.route.overview/title])]
       [:ol {:class (class-names "overflow-y-auto")}
        (doall
         (for [[idx
                {address :address
                 distance :distance
                 duration :duration}]
               (map-indexed vector legs)]
           [:li {:key idx :class "relative"}
            [:span {:class "absolute left-3 border-l border-neutral-50 h-full"}]
            [route-leg idx address distance duration]]))]])))

(defn- driver-fact [label value icon]
  [:span {:class ""}
   [:span {:class "flex items-center text-sm leading-6"}
    [:span {:class "text-neutral-300"} [:> icon {:size 15 :class "mr-1"}]]
    value]
   [:span {:class "sr-only"} label]])

(defn driver-name [title]
  (let [origin (listen [:origin])
        kms (listen [:route/kilometers])
        mins (listen [:route/minutes])]
    (when (some? origin)
      [:div {:class (class-names "flex justify-between w-full")}
       [:div {:class "font-medium text-xl text-left"}
        title
        [:div
         [:div {:class "flex items-center font-light text-xs text-left text-neutral-400"}
          [:div {:class "mr-1 rounded-full w-2 h-2 bg-green-500"}]
          "Last seen at 10:15pm"]]]
       [:div {:class "flex flex-col"}
        [driver-fact (distance-str) [:span kms [:span {:class "text-sm text-neutral-300"} (tr [:units/km])]] DistanceIcon]
        [driver-fact (duration-str) [:span mins [:span {:class "text-sm text-neutral-300"} (tr [:units/min])]] DurationIcon]]])))

(defn inactive-item [name]
  [:div {:class (class-names "flex justify-between w-full")}
   [:div {:class "font-medium text-xl text-left"}
    name
    [:div
     [:div {:class "flex items-center font-light text-xs text-left text-neutral-400"}
      [:div {:class "mr-1 rounded-full w-2 h-2 bg-amber-500"}]
      "Last seen at 3:15pm"]]]
   [button {:label "Schedule route"}]])

(def items [{:name "Edwin Vega"}
            {:name "Billy Armstrong"}
            {:name "Felipe Carapaz"}
            {:name "Walter Van Aert"}])

(def inactive-items [{:name "Diego Wiggins"}
                     {:name "Alfredo Contador"}
                     {:name "Maria Van Vluten"}])

(defn view []
  (fn []
    [:div {:class (class-names padding)}
     [accordion {:items (map (fn [item]
                               {:name [driver-name (:name item)]
                                :description [driver-detail]}) items)
                 :item-class "mb-2 w-full"}]
     (for [item inactive-items]
       ^{:key (:name item)} ;; add real keys!
       [:div {:class (class-names base-button-class "my-2")}
        [inactive-item (:name item)]])]))

