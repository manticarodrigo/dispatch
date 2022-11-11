(ns ui.views.fleet
  (:require
   [react-feather :rename {Check CheckIcon
                           Package PackageIcon
                           User UserIcon
                           ChevronRight ChevronRightIcon
                           Clock DurationIcon}]
   [clojure.string :as s]
   [ui.subs :refer (listen)]
   [ui.lib.router :refer (link)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.button :refer (button button-class base-button-class)]))

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

(defn driver-name [title]
  (let [origin (listen [:origin])]
    (when (some? origin)
      [:div {:class (class-names "flex justify-between w-full")}
       [:div {:class "font-medium text-xl text-left"}
        title
        [:div
         [:div {:class "flex items-center font-light text-xs text-left text-neutral-400"}
          [:div {:class "mr-1 rounded-full w-2 h-2 bg-green-500"}]
          "Last seen at 10:15pm"]]]])))

(defn item [name time status]
  [:div {:class (class-names "flex justify-between w-full")}
   [:div {:class "flex items-center"}
    [:div {:class "mr-2"} [:> UserIcon {:class "w-4 h-4"}]]
    [:div {:class "font-medium text-lg text-left"} name]]
   [:div {:class "flex items-center"}
    [:div {:class "flex flex-col items-end"}
     [:div {:class "flex items-center text-xs text-neutral-400"} "Last seen"]
     [:div {:class "flex items-center text-xs text-left text-neutral-200"}
      [:div {:class (class-names
                     "mr-1 rounded-full w-2 h-2"
                     (if (= status "active") "bg-green-500" "bg-amber-500"))}]
      time]]
    [:div {:class "ml-2"} [:> ChevronRightIcon]]]])

(def items [{:name "Edwin Vega"}
            {:name "Billy Armstrong"}
            {:name "Felipe Carapaz"}
            {:name "Walter Van Aert"}])

(def inactive-items [{:name "Diego Wiggins"}
                     {:name "Alfredo Contador"}
                     {:name "Maria Van Vluten"}])

(defn seat-url [name]
  (str "/seat/" (->> (s/split name #"\s")
                     (s/join "-"))))

(defn view []
  (fn []
    [:ul {:class (class-names padding)}
     (for [{:keys [name]} items]
       ^{:key name} ;; add real keys!
       [:li
        [link {:to (seat-url name)
               :class (class-names "mb-2 block" button-class)}
         [item name "12:15pm" "active"]]])
     (for [{:keys [name]} inactive-items]
       ^{:key name} ;; add real keys!
       [:li
        [link {:to (seat-url name)
               :class (class-names "mb-2 block" button-class)}
         [item name "01/20/2022" "inactive"]]])]))

