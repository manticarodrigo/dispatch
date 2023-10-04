(ns ui.components.inputs.slider
  (:require ["@radix-ui/react-slider" :refer (Root Track Range Thumb)]
            [ui.components.inputs.button :refer (box-class)]
            [ui.utils.string :refer (class-names)]))

(defn thumb [value value-to-label]
  [:> Thumb {:class (class-names
                     "hover:z-10 focus:z-10 active:z-10"
                     "cursor-pointer block rounded-full w-4 h-4"
                     "bg-neutral-200 hover:bg-neutral-100 focus:bg-neutral-50 active:bg-neutral-50"
                     "shadow-sm hover:shadow-md focus:shadow-md active:shadow-lg")}
   [:div {:class (class-names
                  "absolute top-full left-1/2 -translate-x-1/2 translate-y-2"
                  "rounded-md p-1"
                  "text-neutral-50 text-xs font-semibold"
                  "bg-neutral-800/75 backdrop-blur")}
    (if value-to-label
      (value-to-label value)
      value)]])

(defn slider [{:keys [min max step value value-to-label on-change]}]
  [:> Root {:class "relative flex items-center select-none touch-none w-full h-4"
            :value value
            :min min
            :max max
            :step step
            :min-steps-between-thumbs 1
            :on-value-change on-change}
   [:> Track {:class (class-names box-class "relative grow w-full h-2")}
    [:> Range {:class "absolute rounded-full bg-neutral-400 h-full"}]]
   (doall
    (for [[idx v] (map-indexed vector value)]
      ^{:key idx}
      [thumb v value-to-label]))])
