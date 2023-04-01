(ns ui.components.visualizations.bar-chart
  (:require ["@visx/shape" :refer (Bar)]
            ["@visx/group" :refer (Group)]
            ["@visx/scale" :refer (scaleBand scaleLinear)]
            ["@visx/responsive" :refer (withParentSize)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj ->js)]))

(def vertical-margin 30)

(defn bar-chart-base [{:keys [parentWidth parentHeight data get-x get-y]
                       :or {data [] get-x :x get-y :y}}]
  (let [_data (->clj data)
        width parentWidth
        height parentHeight
        x-max width
        y-max (- height vertical-margin)
        x-scale (scaleBand
                 (->js {:range [0 x-max]
                        :round true
                        :domain (map get-x _data)
                        :padding 0.4}))
        y-scale (scaleLinear
                 (->js {:range [y-max 0]
                        :round true
                        :domain [0 (apply max (map get-y _data))]}))]

    (if (< width 10)
      nil
      [:svg {:width width :height height}
       [:> Group {:top (/ vertical-margin 2)}
        (map
         (fn [d]
           (let [x (get-x d)
                 y (get-y d)
                 bar-width (.bandwidth x-scale)
                 bar-height (- y-max (or (y-scale y) 0))
                 bar-x (x-scale x)
                 bar-y (- y-max bar-height)]
             [:> Bar
              {:key (str "bar-" x)
               :x bar-x
               :y bar-y
               :width bar-width
               :height bar-height
               :class "fill-teal-500"
            ;;    :on-click #(when true
            ;;                 (js/alert (str "clicked: "
            ;;                                (-> d
            ;;                                    (js/Object.values)
            ;;                                    (js/JSON.stringify)))))
               }]))
         _data)]])))

(def bar-chart (r/adapt-react-class (withParentSize (r/reactify-component bar-chart-base))))
