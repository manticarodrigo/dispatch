(ns ui.components.inputs.range-slider
  (:require ["react-feather" :rename {Plus PlusIcon
                                      Minus MinusIcon}]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.vector :refer (vec-remove)]
            [ui.components.inputs.input :refer (label-class)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.inputs.slider :refer (slider)]))

(defn range-slider [{:keys [label
                            min
                            max
                            ranges
                            value-to-label
                            add-disabled
                            remove-disabled
                            available-range
                            on-change
                            on-add
                            on-remove]
                     :or {remove-disabled (constantly false)}}]
  (let [all-remove-disabled? (every? remove-disabled (map-indexed vector ranges))]
    [:label
     [:span {:class label-class} label]
     (doall
      (for [[idx range] (map-indexed vector ranges)]
        ^{:key idx}
        [:div {:class "flex items-center mt-4 mb-8"}
         [slider {:min min
                  :max max
                  :step 1
                  :value range
                  :value-to-label value-to-label
                  :on-change (fn [[start end]]
                               (let [prev-range (get ranges (dec idx))
                                     next-range (get ranges (inc idx))
                                     [_ prev-end] prev-range
                                     [next-start _] next-range
                                     current-range-valid (> end start)
                                     prev-range-valid (or
                                                       (nil? prev-range)
                                                       (> start prev-end))
                                     next-range-valid (or
                                                       (nil? next-range)
                                                       (< end next-start))]
                                 (when (and current-range-valid
                                            prev-range-valid
                                            next-range-valid)
                                   (on-change (assoc ranges idx [start end])))))}]
         (when-not all-remove-disabled?
           [:div {:class "ml-2"}
            [button {:type "button"
                     :label [:> MinusIcon {:class "w-4 h-4"}]
                     :disabled (remove-disabled idx range)
                     :on-click #(on-remove (vec-remove ranges idx))}]])]))
     (when-not
      add-disabled
       [button
        {:type "button"
         :label [:div {:class "flex justify-center items-center"}
                 [:> PlusIcon {:class "mr-2 w-4 h-4"}] (tr [:field/add-range])]
         :disabled (not available-range)
         :class (class-names "my-2 border-2 border-dashed w-full")
         :on-click #(on-add (conj ranges available-range))}])]))
