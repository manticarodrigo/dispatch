(ns ui.components.visualizations.world-map
  (:require ["@visx/geo" :refer (Mercator Graticule)]
            ["@visx/responsive" :refer (ParentSize)]
            ["topojson-client" :refer (feature)]
            [reagent.core :as r]
            [shadow.resource :refer (inline)]))

(def background "#f9f7e8")

(defn- feature-shape? [f]
  (= (.-type f) "Feature"))

(defn- topo->geo [topo]
  (let [world (feature topo (-> topo .-objects .-units))
        features (->> (.-features world)
                      (filter feature-shape?))]
    {:type "FeatureCollection" :features features}))

(defn- load-topology []
  (-> (inline "fixtures/world-topology.json")
      js/JSON.parse
      topo->geo))

(def world (load-topology))

(defn world-map [{:keys [class]}]
  [:> ParentSize {:class class}
   (fn [parent]
     (let [width  (.-width parent)
           height (.-height parent)
           center-x (/ width 2)
           center-y (/ height 2)
           scale    (* (/ width 630) 100)]
       (r/as-element
        [:svg {:width width :height height}
         [:> Mercator {:data (:features world)
                       :scale scale
                       :translate [center-x (+ center-y 50)]}
          (fn [m]
            (r/as-element
             [:g
              [:> Graticule {:graticule #(.path m %) :stroke "rgba(255,255,255,0.25)"}]
              (for [f (.-features m)]
                (let [feature (.-feature f)
                      path (.-path f)]
                  [:path {:key (str "map-feature-" (.-id feature))
                          :d (or path "")
                          :stroke "rgba(255,255,255,0.5)"
                          :stroke-width 1
                          :class "fill-slate-600"}]))]))]])))])
