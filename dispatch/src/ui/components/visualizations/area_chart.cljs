(ns ui.components.visualizations.area-chart
  (:require ["@visx/shape" :refer (AreaClosed Line Bar)]
            ["@visx/curve" :rename {curveNatural curve}]
            ["@visx/grid" :refer (GridRows GridColumns)]
            ["@visx/scale" :refer (scaleTime scaleLinear)]
            ["@visx/marker" :refer (MarkerCircle)]
            ["@visx/tooltip" :refer (withTooltip Tooltip TooltipWithBounds defaultStyles)]
            ["@visx/event" :refer (localPoint)]
            ["@visx/gradient" :refer (LinearGradient)]
            ["@visx/axis" :refer (AxisLeft AxisBottom)]
            ["@visx/responsive" :refer (withParentSize)]
            ["d3-array" :refer (extent bisector) :rename {max d3-max}]
            [ui.utils.date :as d]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj ->js)]))

(def background-color "#1e3a8a")
(def accent-color-dark "#1e40af")
(def accent-color "#60a5fa")
(def axis-color "#fafafa")

(def get-date #(-> ^js % .-x js/Date.))
(def get-value #(-> ^js % .-y))

(def format-date #(d/format % "MMM dd, yy"))
(def bisect-date (.-left (bisector #(-> ^js % get-date))))

(defn area-chart-base [{:keys [data margin]
                        :or {margin {:top 20 :right 20 :bottom 40 :left 40}}
                        ;; :or {margin {:top 0 :right 0 :bottom 0 :left 0}}
                        width :parentWidth
                        height :parentHeight
                        format-x :formatX
                        ;; format-y :formatY
                        show-tooltip :showTooltip
                        hide-tooltip :hideTooltip
                        tooltip-data :tooltipData
                        tooltip-top :tooltipTop
                        tooltip-left :tooltipLeft}]
  (let [{:keys [left right top bottom]} margin
        inner-width (- width left right)
        inner-height (- height top bottom)

        x-max (max (+ inner-width left) 0)
        y-max (max (+ inner-height top) 0)
        date-scale (scaleTime
                    (->js {:range [left x-max]
                           :domain (extent data get-date)}))
        value-scale (scaleLinear
                     (->js {:range [y-max top]
                            :domain [0 (or (d3-max data get-value) 0)]
                            :nice true}))
        handle-tooltip (fn [event]
                         (let [x (.-x (localPoint event))
                               x0 (.invert date-scale x)
                               index (bisect-date data x0 1)
                               d0 (aget data (- index 1))
                               d1 (aget data index)
                               d (if (and d1 (get-date d1))
                                   (if (> (- x0 (get-date d0)) (- (get-date d1) x0)) d1 d0) d0)]
                           (show-tooltip
                            #js{:tooltipData d
                                :tooltipLeft x
                                :tooltipTop (value-scale (get-value d))})))]
    (when (>= width 10)
      [:div
       [:svg
        {:width width
         :height height}
        [:> LinearGradient
         {:id "area-gradient"
          :from accent-color
          :to accent-color-dark
          :to-opacity 0}]
        [:> GridRows
         {:left left
          :scale value-scale
          :width inner-width
          :stroke-dasharray "1,3"
          :stroke accent-color
          :stroke-opacity 0
          :pointer-events "none"}]
        [:> GridColumns
         {:top top
          :scale date-scale
          :height inner-height
          :stroke-dasharray "1,3"
          :stroke accent-color
          :stroke-opacity 0.2
          :pointer-events "none"}]
        [:> AreaClosed
         {:data data
          :x #(or (date-scale (get-date %)) 0)
          :y #(or (value-scale (get-value %)) 0)
          :y-scale value-scale
          :stroke-width 3
          :class "stroke-blue-400"
          :fill "url(#area-gradient)"
          :curve curve}]
        (for [[idx d] (map-indexed vector data)]
          ^{:key idx}
          [:circle
           {:r 5
            :cx (date-scale (get-date d))
            :cy (value-scale (get-value d))
            :class "fill-blue-400"}])
        [:> Bar
         {:x left
          :y top
          :width inner-width
          :height inner-height
          :fill "transparent"
          :on-touch-start handle-tooltip
          :on-touch-move handle-tooltip
          :on-mouse-move handle-tooltip
          :on-mouse-leave #(hide-tooltip)}]
        (when tooltip-data
          [:g
           [:> Line
            {:from {:x tooltip-left :y top}
             :to {:x tooltip-left :y (+ inner-height top)}
             :stroke accent-color-dark
             :stroke-width 2
             :pointer-events "none"
             :stroke-dasharray "5,2"}]
           [:circle
            {:cx tooltip-left
             :cy (+ tooltip-top 1)
             :r 4
             :fill "black"
             :fill-opacity 0.1
             :stroke "black"
             :stroke-opacity 0.1
             :stroke-width 2
             :pointer-events "none"}]
           [:circle
            {:cx tooltip-left
             :cy tooltip-top
             :r 4
             :fill accent-color-dark
             :stroke "white"
             :stroke-width 2
             :pointer-events "none"}]])
        [:> AxisBottom
         {:top y-max
          :scale date-scale
          :num-ticks 4
          :tick-format format-date
          :stroke axis-color
          :tick-stroke axis-color
          :tick-label-props
          {:text-anchor "middle"
           :font-size 10
           :dx "0em"
           :dy "0.32em"
           :fill axis-color}}]
        [:> AxisLeft
         {:scale value-scale
          :left left
          :num-ticks 4
          :tick-format format-x
          :stroke axis-color
          :tick-stroke axis-color
          :tick-label-props
          {:text-anchor "end"
           :font-size 10
           :dx "-0.25em"
           :dy "0.32em"
           :fill axis-color}}]]

       (when tooltip-data
         [:div
          [:> TooltipWithBounds
           {:key (rand)
            :top (- tooltip-top 12)
            :left (+ tooltip-left 12)
            :style (assoc (->clj defaultStyles)
                          :background background-color
                          :color axis-color)}
           (format-x (get-value tooltip-data))]
          [:> Tooltip
           {:top (- (+ inner-height top) 0)
            :left tooltip-left
            :style (merge (->clj defaultStyles)
                          {:min-width 90
                           :text-align "center"
                           :background accent-color
                           :color background-color
                           :transform "translateX(-50%)"})}
           (format-date (get-date tooltip-data))]])])))

(def area-chart
  (r/adapt-react-class
   (withParentSize
    (withTooltip
     (r/reactify-component area-chart-base)))))
