(ns ui.views.seat
  (:require
   [react-feather :rename {ChevronLeft ChevronLeftIcon
                           Check CheckIcon
                           Package PackageIcon
                           Clock DurationIcon}]
   [ui.subs :refer (listen)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]))

(defn- route-leg [idx address]
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

(defn view []
  (let [legs (listen [:route/legs])]
    [:div {:class (class-names padding)}
     (when (> (count legs) 0)
       [:section {:class (class-names "p-2.5")}
        [:h2 {:class (class-names "sr-only")}
         (tr [:view.route.overview/title])]
        [:ol {:class (class-names "overflow-y-auto")}
         (doall
          (for [[idx {address :address}] (map-indexed vector legs)]
            [:li {:key idx :class "relative"}
             [:span {:class "absolute left-3 border-l border-neutral-50 h-full"}]
             [route-leg idx address]]))]])]))
