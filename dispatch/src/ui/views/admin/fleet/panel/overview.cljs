(ns ui.views.admin.fleet.panel.overview
  (:require
   [react-feather :rename {GitPullRequest DistanceIcon
                           Clock DurationIcon}]
   [ui.subs :refer (listen)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (distance-str duration-str padding)]
   [ui.components.generic.accordion :refer (accordion)]))


(defn- route-leg-fact [label value]
  [:div {:class "flex justify-center items-center mb-1 text-neutral-300"}
   [:span {:class "sr-only"} label " "]
   value])


(defn- route-leg [idx address distance duration]
  [:div {:class (class-names  "py-2 flex")}
   [:div {:class (class-names "shrink-0 flex justify-center items-center"
                              "rounded-full"
                              "w-8 h-8"
                              "font-bold text-neutral-50 bg-neutral-700")} (+ 1 idx)]
   [:p {:class "grow px-2 md:px-4 lg:px-6 text-sm leading-4"} address]
   [:div {:class "shrink-0 flex flex-col items-end text-sm leading-4"}
    [route-leg-fact (distance-str) (str (js/Math.round (/ distance 1000)) " " (tr [:units/kilometers]))]
    [route-leg-fact (duration-str) (str (js/Math.round (/ duration 60)) " " (tr [:units/minutes]))]]])

(defn driver-detail []
  (let [legs (listen [:route/legs])]
    (when (> (count legs) 0)
      [:section {:class (class-names "p-3.5")}
       [:h2 {:class (class-names "sr-only")}
        (tr [:view.route.overview/title])]
       [:ol {:class (class-names "overflow-y-auto")}
        (doall
         (for [[idx
                {address :address
                 distance :distance
                 duration :duration}]
               (map-indexed vector legs)]
           [:li {:key idx}
            [route-leg idx address distance duration]]))]])))

(defn- driver-fact [label value icon]
  [:span {:class "pr-3"}
   [:span {:class "flex items-center text-lg leading-6"}
    [:span {:class "text-neutral-300"} [:> icon {:size 18 :class "mr-1"}]]
    value]
   [:span {:class "sr-only"} label]])

(defn driver-name [title]
  (let [origin (listen [:origin])
        kms (listen [:route/kilometers])
        mins (listen [:route/minutes])]
    (when (some? origin)
      [:div {:class (class-names "p-2")}
       [:div {:class "font-medium text-xl text-left"} title]
       [:div {:class "flex"}
        [driver-fact (distance-str) [:span kms [:span {:class "text-sm text-neutral-300"} (tr [:units/km])]] DistanceIcon]
        [driver-fact (duration-str) [:span mins [:span {:class "text-sm text-neutral-300"} (tr [:units/min])]] DurationIcon]]])))

(def items [{:name [driver-name "Edwin V"]
             :description [driver-detail]}
            {:name [driver-name "Billy M"]
             :description [driver-detail]}
            {:name [driver-name "Felipe C"]
             :description [driver-detail]}
            {:name [driver-name "Alfredo C"]
             :description [driver-detail]}
            {:name [driver-name "Maria P"]
             :description [driver-detail]}])

(defn overview []
  [:div {:class (class-names padding "overflow-y-auto")}
   [accordion {:items items :item-class "my-2 w-full"}]])
