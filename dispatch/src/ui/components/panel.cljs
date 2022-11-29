(ns ui.components.panel
  (:require ["framer-motion" :refer [motion useAnimation]]
            [react :as react]
            [reagent.core :as r]
            [ui.utils.string :refer (class-names)]
            [ui.hooks.use-media-query :refer (use-media-query lg)]
            [ui.hooks.use-window-size :refer (use-window-size)]
            [ui.components.header :refer (header)]))

(defn panel-mobile [class children]
  (let [!showing (r/atom false)]
    (fn []
      (let [controls (useAnimation)
            window-size (use-window-size)
            bottom (- (:height window-size) 100)]
        (react/useEffect
         (fn []
           (.start controls (if @!showing "visible" "hidden"))
           #())
         #js[controls @!showing window-size])
        [:> (.-div motion)
         {:initial "hidden"
          :animate controls
          :variants {:visible {:y 0}
                     :hidden {:y bottom}}
          :transition {:type "spring"
                       :damping 20
                       :stiffness 100}
          :drag "y"
          :drag-elastic 0.1
          :drag-constraints {:top 0 :bottom bottom}
          :on-drag-end (fn [_ info]
                         (let [velocity (.. info -velocity -y)
                               point (.. info -point -y)
                               should-close? (or
                                              (> velocity 20)
                                              (and (>= velocity 0)
                                                   (> point 45)))]
                           (reset! !showing (not should-close?))))
          :class (class-names
                  class
                  "z-10 fixed lg:hidden"
                  "w-full h-full bg-neutral-900")}
         [:div {:on-click #(swap! !showing not)
                :class "flex justify-center items-center p-4 w-full"}
          [:span {:class "w-16 h-1 m-0.5 rounded-full bg-neutral-50"}]]
         [:div {:class "w-full h-[calc(100%_-_2.5rem)]"}
          [:div {:on-click #(reset! !showing true)}
           [header]]
          children]]))))

(defn panel-desktop [class children]
  [:div {:class
         (class-names
          class
          "flex-none hidden lg:block"
          "w-[450px] h-full")}
   [header]
   children])

(defn panel [class & children]
  (let [lg? (use-media-query lg)]
    [:<>
     (if lg?
       [panel-desktop class (into [:<>] children)]
       [panel-mobile class (into [:<>] children)])]))
