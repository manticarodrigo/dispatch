(ns app.views.route.nav.overview
  (:require
   [app.subs :refer (listen)]
   [app.utils.i18n :refer (tr)]
   [app.utils.string :refer (class-names)]
   [app.views.route.utils :refer (distance-str
                                  duration-str
                                  padding)]))

(defn- overview-item-details [label value]
  [:div {:class "flex justify-center items-center mb-1 text-neutral-300"}
   [:span {:class "sr-only"} label " "]
   value])

(defn- overview-item-number [num]
  [:div {:class
         (class-names "flex justify-center items-center"
                      "rounded-full"
                      "w-8 h-8"
                      "font-bold text-neutral-50 bg-neutral-700")} num])


(defn- overview-item [idx address distance duration]
  [:div {:class (class-names  "py-2 flex")}
   [:div {:class "shrink-0"} [overview-item-number (+ 1 idx)]]
   [:p {:class "grow px-2 md:px-4 lg:px-6 text-sm leading-4"} address]
   [:div {:class "shrink-0 flex flex-col items-end text-sm leading-4"}
    [overview-item-details (distance-str) (str (js/Math.round (/ distance 1000)) " " (tr [:units/kilometers]))]
    [overview-item-details (duration-str) (str (js/Math.round (/ duration 60)) " " (tr [:units/minutes]))]]])

(defn overview [class]
  (let [legs (listen [:route/legs])]
    (when (> (count legs) 0)
      [:section {:class (class-names padding)}
       [:h2 {:class (class-names "mb-4 flex font-medium text-l")}
        (tr [:view.route.overview/title])]
       [:ol {:class (class-names class "overflow-y-auto")}
        (doall
         (for [[idx
                {address :address
                 distance :distance
                 duration :duration}]
               (map-indexed vector legs)]
           [:li {:key idx}
            [overview-item idx address distance duration]]))]])))
