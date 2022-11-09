(ns ui.views.fleet
  (:require
   [react-feather :rename {PlusCircle PlusIcon
                           GitPullRequest DistanceIcon
                           Clock DurationIcon}]
   [reagent.core :as r]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.button :refer (button base-button-class)]
   [ui.components.inputs.generic.modal :refer (modal)]
   [ui.components.forms.user :refer (user-form)]
   [ui.components.forms.route :refer (route-form)]
   [ui.subs :refer (listen)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.accordion :refer (accordion)]))

(defonce distance-str #(tr [:view.fleet/distance]))
(defonce duration-str #(tr [:view.fleet/duration]))

(defn- route-leg-fact [label value]
  [:div {:class "flex justify-center items-center mb-1 text-neutral-300"}
   [:span {:class "sr-only"} label " "]
   value])


(defn- route-leg [idx address distance duration]
  [:div {:class (class-names  "py-2 flex")}
   [:div {:class (class-names "relative"
                              "shrink-0 flex justify-center items-center"
                              "rounded-full border border-neutral-300"
                              "w-8 h-8"
                              "font-bold bg-neutral-900")}
    (+ 1 idx)]
   [:p {:class "grow px-2 md:px-3 lg:px-4 text-sm leading-4"} address]
   [:div {:class "shrink-0 flex flex-col items-end text-sm leading-4"}
    [route-leg-fact (distance-str) (str (js/Math.round (/ distance 1000)) " " (tr [:units/km]))]
    [route-leg-fact (duration-str) (str (js/Math.round (/ duration 60)) " " (tr [:units/min]))]]])

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
            [:span {:class "absolute left-4 border-l border-neutral-50 h-full"}]
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
        [:div {:class "font-light text-xs text-left text-neutral-400"}
         "Departed at: 10:15pm"]]

       [:div {:class "flex flex-col"}
        [driver-fact (distance-str) [:span kms [:span {:class "text-sm text-neutral-300"} (tr [:units/km])]] DistanceIcon]
        [driver-fact (duration-str) [:span mins [:span {:class "text-sm text-neutral-300"} (tr [:units/min])]] DurationIcon]]])))

(def items [{:name [driver-name "Edwin Vega"]
             :description [driver-detail]}
            {:name [driver-name "Billy Armstrong"]
             :description [driver-detail]}
            {:name [driver-name "Felipe Carapaz"]
             :description [driver-detail]}
            {:name [driver-name "Diego Wiggins"]
             :description [driver-detail]}
            {:name [driver-name "Alfredo Contador"]
             :description [driver-detail]}
            {:name [driver-name "Maria Van Vluten"]
             :description [driver-detail]}])

(defn view []
  (let [!create-route? (r/atom false)
        !selected-seat (r/atom nil)]
    (fn []
      [:div {:class (class-names padding)}
       [modal {:show (some? @!selected-seat)
               :title (if (empty? @!selected-seat) "Add seat" "Manage seat")
               :on-close #(reset! !selected-seat nil)}
        [user-form {:initial-state @!selected-seat}]]

       [button {:label "Add seat"
                :class "mb-4 w-full"
                :on-click #(reset! !selected-seat {})}]
       [accordion {:items items :item-class "mb-2 w-full"}]])))

