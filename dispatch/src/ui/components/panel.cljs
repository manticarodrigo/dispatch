(ns ui.components.panel
  (:require ["framer-motion" :refer [motion]]
            [reagent.core :as r]
            [ui.utils.string :refer (class-names)]))

(defn panel-mobile [class children]
  (let [!show (r/atom false)]
    (fn []
      (prn @!show)
      [:> (.-section motion)
       {:animate {:translateY (if @!show 0 "calc(100% - 40px)")}
        :transition {:type "spring",
                     :damping 20,
                     :stiffness 100}
        :drag "y"
        :dragConstraints {:top 0 :bottom 0}
        :dragElastic 0.2
        :onDragEnd (fn [_ info]
                     (if @!show
                       (when (< 0 (.. info -offset -y))
                         (reset! !show false))
                       (when (> 0 (.. info -offset -y))
                         (reset! !show true))))
        :class (class-names
                class
                "z-10 absolute lg:hidden"
                "w-full h-[calc(100%_-_60px)] bg-neutral-900")}
       [:button {:on-click (fn []
                             (swap! !show not))
                 :class "flex justify-center items-center p-4 w-full"}
        [:span {:class "w-16 h-1 m-0.5 rounded-full bg-neutral-50"}]]
       [:div {:class "w-full h-[calc(100%_-_2.5rem)] overflow-y-auto"}
        children]])))

(defn panel-desktop [class children]
  [:section {:class
             (class-names
              class
              "flex-none hidden lg:block"
              "w-[450px] h-full overflow-y-auto")}
   children])

(defn panel [class & children]
  [:<>
   [panel-mobile class (into [:<>] children)]
   [panel-desktop class (into [:<>] children)]])
