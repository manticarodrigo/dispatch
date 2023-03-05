(ns ui.hooks.use-map
  (:require [react :refer (useEffect)]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [promesa.core :as p]
            [cljs-bean.core :refer (->js)]
            [ui.subs :refer (listen)]
            [ui.lib.google.maps.core :refer (init-api)]
            [ui.lib.google.maps.polyline :refer (set-polylines clear-polylines)]
            [ui.lib.google.maps.marker :refer (set-markers clear-markers)]
            [ui.lib.google.maps.overlay :refer (set-overlays clear-overlays)]
            [ui.utils.location :refer (position-to-lat-lng)]))

(def ^:private !el (atom nil))
(def ^:private !map (r/atom false))

(defn- center []
  (when-let [^js gmap @!map]
    (let [{:keys [paths points locations]} (listen [:map])
          {:keys [position]} (listen [:device])
          coords (flatten (concat
                           paths
                           (map :position points)
                           (map
                            #(-> % :position position-to-lat-lng)
                            (remove nil? (conj locations position)))))
          bounds (js/google.maps.LatLngBounds.)]
      (if (seq coords)
        (do
          (doseq [{:keys [lat lng]} coords]
            (.extend bounds #js{:lat lat :lng lng}))
          (.fitBounds gmap bounds)
          (.panToBounds gmap bounds)
          (when (> (.getZoom gmap) 15)
            (.setZoom gmap 15)))
        (do (.setZoom gmap 2)
            (.setCenter gmap #js{:lat 0 :lng 0}))))))

(defn use-map []
  (useEffect
   (fn []
     (p/let [gmap (init-api @!el)]
       (reset! !map gmap))
     #())
   #js[])
  !el)

(defn map-ref [root-node]
  (let [map-node (.getElementById js/document "map-container")
        map-bench-node (.getElementById js/document "map-bench")]
    (when map-node
      (if root-node
        (.replaceWith root-node map-node)
        (when map-bench-node
          (.appendChild map-bench-node map-node))))))

(defn use-map-render []
  (let [{:keys [paths points locations]} (listen [:map])
        {:keys [position]} (listen [:device])]

    (useEffect
     (fn []
       (if @!map (let [polylines (when (seq paths) (set-polylines @!map paths))
                       markers (when (seq points) (set-markers @!map points))
                       overlays (when (or (seq locations) position)
                                  (set-overlays @!map (remove nil? (conj locations position))))]
                   #(do
                      (clear-polylines polylines)
                      (clear-markers markers)
                      (clear-overlays overlays)))
           #()))
     #js[@!map paths points locations position])

    {:ref map-ref
     :center center}))

(defn use-map-items [loading items deps]
  (let [tasks (->> items :tasks (filterv some?))
        places (->> items :places (filterv some?))
        agents (->> items :agents (filterv some?))]
    (useEffect
     (fn []
       (dispatch
        [:map
         {:paths
          (filterv
           some?
           (mapv #(-> % :route :path) tasks))
          :points
          (filterv
           some?
           (mapv
            (fn [{:keys [lat lng name]}]
              {:title name
               :position {:lat lat :lng lng}})
            places))
          :locations
          (filterv
           some?
           (mapv
            (fn [{:keys [name location]}]
              {:title name
               :position (:position location)})
            (filter #(:location %) agents)))}])

       (when-not loading
         (js/setTimeout center 500))
       #())
     (->js deps))))
