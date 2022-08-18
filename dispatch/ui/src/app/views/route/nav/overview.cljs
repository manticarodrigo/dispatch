(ns app.views.route.nav.overview
  (:require
   [app.subs :refer (listen)]
   [app.utils.string :refer (class-names)]
   [app.views.route.utils :refer (distance-str
                                  duration-str
                                  padding-x)]))

(defn- overview-item-details [label value]
  [:div {:class "flex justify-center items-center mb-1 text-neutral-300"}
   [:span {:class "sr-only"} label " "]
   value])

(defn- overview-item-number [num]
  [:div {:class
         (class-names "flex justify-center items-center"
                      "rounded-full"
                      "w-8 h-8"
                      "font-bold text-neutral-50 bg-neutral-900")} num])

(defn- overview-item [idx address distance duration]
  [:div {:class (class-names padding-x "flex py-2 sm:py-3 md:py-4 lg:py-5 hover:bg-neutral-900")}
   [:div {:class "shrink-0"} [overview-item-number (+ 1 idx)]]
   [:div {:class "grow px-2 md:px-4 lg:px-6 font-medium text-sm leading-4"} address]
   [:div {:class "shrink-0 flex flex-col items-end text-sm leading-4"}
    [overview-item-details (distance-str) (:text distance)]
    [overview-item-details (duration-str) (:text duration)]]])

(defn overview [class]
  (let [route (listen [:route])]
    (when (> (count route) 0)
      [:ol {:class (class-names
                    class
                    "overflow-y-auto")}
       (doall
        (for [[idx
               {address :address
                distance :distance
                duration :duration}]
              (map-indexed vector route)]
          [:li {:key idx}
           [overview-item idx address distance duration]]))])))
