(ns ui.components.icons.dispatch)

(defn dispatch-base [{width :width height :height :or {width 24 height 24}}]
  [:svg {:xmlns "http://www.w3.org/2000/svg"
         :viewBox "0 0 665.26 665.2"
         :fill "currentColor"
         :width width
         :height height}
   [:path {:d "M332.65,0H133.09V66.52H332.65c146.72,0,266.08,119.36,266.08,266.08S479.37,598.68,332.65,598.68H133.09V665.2H332.65c183.4,0,332.61-149.2,332.61-332.6S516.05,0,332.65,0Z"}]
   [:path {:d "M133,266.05V532.13H332.6c110,0,199.56-89.52,199.56-199.56S442.64,133,332.6,133H99.78A99.9,99.9,0,0,0,0,232.79V532.13H66.52V232.79a33.3,33.3,0,0,1,33.26-33.26H332.6c73.36,0,133,59.68,133,133s-59.68,133-133,133h-133V266.05Z"}]])

(defn dispatch
  ([] [dispatch-base {}])
  ([props] [dispatch-base props]))
