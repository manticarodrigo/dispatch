(ns ui.components.visualizations.pie-chart
  (:require ["@visx/shape" :refer (Pie)]
            ["@visx/scale" :refer (scaleOrdinal)]
            ["@visx/group" :refer (Group)]
            ["@visx/legend" :refer (LegendItem LegendLabel LegendOrdinal)]
            ["@visx/responsive" :refer (ParentSize)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj ->js)]))

(defn animated-pie
  [{:keys [pie get-key get-percentage get-color on-click-datum]}]
  [:<>
   (for [arc (.-arcs pie)]
     (let [key (get-key arc)
           [centroid-x centroid-y] (-> pie .-path (.centroid arc))
           has-space-for-label (>= (- (.-endAngle arc) (.-startAngle arc)) 0.1)]
       [:g {:key key}
        [:path {:d (.path pie arc)
                :fill (get-color arc)
                :on-click #(on-click-datum arc)
                :on-touch-start #(on-click-datum arc)}]
        (when has-space-for-label
          [:g
           [:text {:fill "white"
                   :x centroid-x
                   :y centroid-y
                   :dy ".33em"
                   :fontSize 9
                   :textAnchor "middle"
                   :pointerEvents "none"}
            (js/Math.round (get-percentage arc)) "%"]])]))])

(defn legend [scale]
  [:div {:class "flex justify-center items-center w-full min-w-0"}
   [:> LegendOrdinal {:scale scale}
    (fn [labels]
      (r/as-element
       [:div {:class "max-w-full max-h-full overflow-auto"}
        (for [label labels]
          (let [i (.-index label)
                legend-glyph-size 15]
            (r/as-element
             [:> LegendItem
              {:key (str "legend-ordinal-" i)
               :margin "0 5px"
               :on-click #()}
              [:svg {:width legend-glyph-size :height legend-glyph-size}
               [:rect {:fill (.-value label)
                       :width legend-glyph-size
                       :height legend-glyph-size}]]
              [:> LegendLabel
               {:align "left" :margin "0 0 0 4px"}
               (.-text label)]])))]))]])

(defn pie-chart [{:keys [data margin get-label get-scale] :or {margin {:top 20 :right 20 :bottom 20 :left 20}
                                                               get-label :label
                                                               get-scale :percent}}]
  (let [{:keys [top right bottom left]} margin
        data-labels (map get-label data)
        pie-scale (scaleOrdinal (->js {:domain data-labels
                                       :range ["rgba(59,130,246,0.7)"
                                               "rgba(59,130,246,0.6)"
                                               "rgba(59,130,246,0.5)"
                                               "rgba(59,130,246,0.4)"
                                               "rgba(59,130,246,0.3)"
                                               "rgba(59,130,246,0.2)"
                                               "rgba(59,130,246,0.1)"]}))]
    [:div {:class "flex w-full h-full"}
     [:> ParentSize
      (fn [parent]
        (let [width (.-width parent)
              height (.-height parent)
              inner-width (- width left right)
              inner-height (- height top bottom)
              radius (/ (min inner-width inner-height) 2)
              center-y (/ inner-height 2)
              center-x (/ inner-width 2)
              donut-thickness 50]
          (r/as-element
           (when (> width 10)
             [:svg {:width width :height height}
              [:> Group {:top (+ center-y top) :left (+ center-x left)}
               [:> Pie {:data data
                        :pie-value #(get-scale (->clj %))
                        :outer-radius radius
                        :corner-radius 3
                        :inner-radius (- radius donut-thickness)
                        :pad-angle 0.025}
                (fn [pie]
                  (r/as-element
                   [animated-pie
                    {:pie pie
                     :get-key (fn [arc] (get-label (->clj (.-data arc))))
                     :get-percentage (fn [arc] (get-scale (->clj (.-data arc))))
                     :get-color (fn [arc] (pie-scale (get-label (->clj (.-data arc)))))
                     :on-click-datum #()}]))]]]))))]
     [legend pie-scale]]))
