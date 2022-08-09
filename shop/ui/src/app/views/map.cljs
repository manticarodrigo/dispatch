(ns app.views.map
  (:require
   [re-frame.core :as rf]
   [app.components.map :as map]
   [app.subs]))

(defn page []
  (let [!location (rf/subscribe [:location/current])
        {coords :coords zoom :zoom} @!location
        {lat :lat lng :lng} coords]
    [:div {:class "w-full h-screen bg-blue-600"}
     [:div {:class "z-[10] absolute bottom-2 left-2 p-2 rounded bg-white drop-shadow"}
      "Lat: " lat ", Lng: " lng ", Zoom: " zoom]
     [map/component]]))
