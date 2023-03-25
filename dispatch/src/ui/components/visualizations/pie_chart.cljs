(ns ui.components.visualizations.pie-chart
  (:require [reagent.core :as r]
            ["@visx/shape" :refer (Pie)]
            ["@visx/scale" :refer (scaleOrdinal)]
            ["@visx/group" :refer (Group)]
            ["@visx/mock-data" :refer (letterFrequency browserUsage)]
            ["@visx/annotation" :refer (Annotation Connector Label)]
            ["@visx/responsive" :refer (withParentSize)]))

;; data and types
(def letters (.slice letterFrequency 0 4))
(def browser-names (remove #(= "date" %) (js-keys (first browserUsage))))
(def browsers (map (fn [name] #js {:label name :usage (aget (first browserUsage) name)}) browser-names))

;; accessor functions
(defn usage [^js d] (.-usage d))
(defn frequency [^js d] (.-frequency d))

;; color scales
(def get-browser-color
  (scaleOrdinal #js {:domain browser-names
                     :range ["rgba(59,130,246,0.7)"
                             "rgba(59,130,246,0.6)"
                             "rgba(59,130,246,0.5)"
                             "rgba(59,130,246,0.4)"
                             "rgba(59,130,246,0.3)"
                             "rgba(59,130,246,0.2)"
                             "rgba(59,130,246,0.1)"]}))

(def from-leave-transition
  (fn [{:keys [end-angle]}] #js {:start-angle (if (> end-angle js/Math.PI) (* 2 js/Math.PI) 0)
                                 :end-angle (if (> end-angle js/Math.PI) (* 2 js/Math.PI) 0)
                                 :opacity 0}))

(def enter-update-transition
  (fn [{:keys [start-angle end-angle]}] #js {:start-angle start-angle
                                             :end-angle end-angle
                                             :opacity 1}))

(defn animated-pie
  [{:keys [pie get-key get-color on-click-datum]}]
  (js/console.log pie)
  [:<>
   (for [arc (.-arcs pie)]
     (let [space-for-label? (>= (- (.-endAngle arc) (.-startAngle arc)) 0.1)
           key (get-key arc)
           [centroid-x centroid-y] (.centroid (.-path pie) arc)
           angle (-> (+ (.-startAngle arc) (.-endAngle arc) (.-padAngle arc)) (/ 2))
           label-offset 20]
       (prn angle)
       [:g {:key key}
        [:path {:d (.path pie arc)
                :fill (get-color arc)
                :on-click #(on-click-datum arc)
                :on-touch-start #(on-click-datum arc)}]
        (when space-for-label?
          [:> Annotation
           {:x centroid-x
            :y centroid-y
            :dx (if (< angle js/Math.PI) label-offset (- label-offset))
            :dy (if (< angle js/Math.PI) label-offset (- label-offset))
            ;; :dx dx
            ;; :dy dy
            }
           [:> Connector
            {:stroke "white"
             :stroke-width 1
             :type "elbow"}]
           [:> Label
            {:title (get-key arc)
             :fill "white"
             :font-size 9
             :text-anchor "middle"}]])]))])

(defn pie-chart-base
  [{:keys [margin]
    width :parentWidth
    height :parentHeight
    :or {margin #js{:top 20 :right 20 :bottom 20 :left 20}}}]
  (let [inner-width (- width (.-left margin) (.-right margin))
        inner-height (- height (.-top margin) (.-bottom margin))
        radius (/ (min inner-width inner-height) 2)
        center-y (/ inner-height 2)
        center-x (/ inner-width 2)
        donut-thickness 50]
    (if (< width 10)
      nil
      [:svg {:width width :height height}
       [:> Group {:top (+ center-y (.-top margin)) :left (+ center-x (.-left margin))}
        [:> Pie {:data browsers
                 :pie-value usage
                 :outer-radius radius
                 :inner-radius (- radius donut-thickness)
                 :pad-angle 0.005}
         (fn [pie]
           (r/as-element
            [animated-pie {:pie pie
                           :get-key (fn [arc] (str (.-usage (.-data arc)) "%"))
                           :get-color (fn [arc] (get-browser-color (.-label (.-data arc))))
                           :on-click-datum (fn [arc])}]))]]])))

(def pie-chart (r/adapt-react-class (withParentSize (r/reactify-component pie-chart-base))))
