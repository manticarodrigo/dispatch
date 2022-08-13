(ns app.views.gmap
  (:require
   [goog.string :refer (format)]
   [app.components.gmap :as gmap]
   [app.subs :as subs]))

(defn- millionths [num] (format "%.4f" num))

(defn view []
  (let [location (subs/listen [:location/current])]
    [:div {:class "w-full h-screen bg-blue-600"}
     [:div {:class "z-[10] absolute top-2 left-2 p-2 rounded bg-slate-700 drop-shadow"}
      [:div {:class "text-md text-slate-200"} "Location"]
      [:div {:class "text-sm text-slate-50"}
       (if-let [{lat :lat lng :lng} location]
         [:<> (millionths lat) ", " (millionths lng)]
         "Not available.")]]
     [gmap/component]]))
