(ns app.views.map
  (:require
   [reagent.core :as r]
   [app.config :as config]
   ["@capacitor/google-maps" :refer (GoogleMap)]))


(defn- create-map [el]
  (.create GoogleMap
           (clj->js {:id "portal-map"
                     :element el
                     :apiKey (config/env :google-maps-api-key)
                     :config {:center {:lat 33.6 :lng -117.9}
                              :zoom 8}})))

(defn- map-view []
  (let [!map (clojure.core/atom nil)]
    (r/create-class
     {:component-did-mount (fn [] (create-map @!map))
      :reagent-render
      (fn []
        [:capacitor-google-map
         {:ref (fn [el] (reset! !map el))
          :style {:display "block"
                  :width "100%"
                  :height "100%"}}])})))

(defn page []
  [:div {:class "w-full h-screen bg-blue-600"}
   [map-view]])
