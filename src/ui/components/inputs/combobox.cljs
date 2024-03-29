(ns ui.components.inputs.combobox
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {ChevronDown ChevronDownIcon}]
            [headlessui-reagent.core :as ui]
            [clojure.string :as s]
            [ui.lib.floating :refer (use-floating)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.input :refer (label-class input-class)]
            [ui.components.inputs.menu :refer (menu-class
                                               menu-item-class
                                               menu-item-active-class)]))

(defn combobox [{aria-label :aria-label
                 label :label
                 placeholder :placeholder
                 value :value
                 required :required
                 class :class
                 options :options
                 option-to-label :option-to-label
                 option-to-value :option-to-value
                 option-to-render :option-to-render
                 on-text :on-text
                 on-change :on-change
                 on-select :on-select
                 :or {option-to-value #(:value %)
                      option-to-label #(:label %)}}]
  (let [{:keys [x y reference floating strategy]} (use-floating)
        [query set-query] (useState "")
        filtered-options (filter #(-> %
                                      option-to-label
                                      s/lower-case
                                      (s/starts-with? (s/lower-case query)))
                                 options)
        on-query (fn [e]
                   (if on-text
                     (on-text (-> e .-target .-value))
                     (set-query (-> e .-target .-value))))]
    [ui/combobox
     {:as "div"
      :class (class-names class "relative")
      :value value
      :on-change (fn [option]
                   (when on-change (on-change option))
                   (when (and option-to-value on-select)
                     (on-select (some
                                 #(when (= option (option-to-value %)) %)
                                 options))))}

     [ui/combobox-label {:class "block"}
      [:span
       {:class (if aria-label
                 "sr-only"
                 (class-names
                  label-class
                  (when required
                    "after:content-['*'] after:ml-0.5 after:text-red-500")))}
       (or aria-label label)]
      [ui/combobox-button
       {:as "div"
        :ref reference
        :class "relative"}
       [ui/combobox-input
        {:placeholder placeholder
         :required required
         :class input-class
         :style {:padding-right "3rem"}
         :display-value (fn [v]
                          (option-to-label
                           (first (filter #(= v (option-to-value %)) options))))
         :on-change on-query}]
       [:div {:class "cursor-text absolute top-[50%] translate-y-[-50%] right-0 pr-2 lg:pr-4"}
        [:> ChevronDownIcon {:class "w-4 h-4"}]]]]

     [ui/transition
      {:leave "transition ease-in duration-100"
       :leave-from "opacity-100"
       :leave-to "opacity-0"}
      [ui/combobox-options
       {:ref floating
        :style {:position strategy
                :top (or y 0)
                :left (or x 0)}
        :class (class-names
                menu-class
                "z-10"
                "max-h-60 w-full")}
       [:<>
        (when (= (count filtered-options) 0)
          [:div {:class menu-item-class}
           (tr [:misc/empty-search])])

        (for [option filtered-options]
          ^{:key (option-to-value option)}
          [ui/combobox-option {:value (option-to-value option)
                               :class (fn [{:keys [active selected]}]
                                        (class-names
                                         menu-item-class
                                         (when active menu-item-active-class)
                                         (when selected "underline")))}
           ((or option-to-render option-to-label) option)])]]]]))
