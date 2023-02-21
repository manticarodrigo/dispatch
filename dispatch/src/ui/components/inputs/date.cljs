(ns ui.components.inputs.date
  (:require ["@floating-ui/react-dom" :refer (useFloating offset flip shift)]
            [react-feather :rename {ChevronLeft ChevronLeftIcon
                                    ChevronRight ChevronRightIcon}]
            [use-lilius :refer (useLilius)]
            [cljs-bean.core :refer (->clj)]
            [headlessui-reagent.core :as ui]
            [ui.utils.date :as d]
            [ui.utils.string :refer (class-names)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.menu :refer (menu-class)]
            [ui.components.inputs.button :refer (button button-class)]))

(defn date-select [{:keys [class label value on-select]}]
  (let [{:keys [x y reference floating strategy]}
        (->clj
         (useFloating
          #js{:placement "bottom"
              :middleware #js[(offset 10)
                              (shift #js{:padding 10})
                              (flip)]}))
        {:keys [calendar
                isSelected
                select
                viewing
                viewNextMonth
                viewPreviousMonth]}
        (->clj
         (useLilius (if value #js{:selected #js[value]} #js{})))]
    [ui/popover {:class (class-names class "relative")}
     [ui/popover-button {:ref reference
                         :aria-label label
                         :class (class-names button-class "w-full")}
      (if value
        (d/format value "dd/MM/yyyy")
        (or label (tr [:field/date])))]
     [ui/popover-panel {:ref floating
                        :style {:position strategy
                                :top (or y 0)
                                :left (or x 0)}
                        :class (class-names menu-class "z-10")}
      [:div {:class "p-2"}
       [:div {:class "flex items-center"}
        [button {:aria-label (tr [:calendar/previous-month])
                 :label [:> ChevronLeftIcon]
                 :class "!p-1"
                 :on-click viewPreviousMonth}]
        [:p {:class "flex-1 text-center capitalize"}
         (d/format viewing "MMMM yyyy")]
        [button {:aria-label (tr [:calendar/next-month])
                 :label [:> ChevronRightIcon]
                 :class "!p-1"
                 :on-click viewNextMonth}]]
       [:div {:class "flex"}
        (doall
         (for [day (-> calendar first first)]
           ^{:key day}
           [:div {:class "flex justify-center items-center w-10 h-10"}
            [:span {:class "font-light text-neutral-300 capitalize"}
             (d/format day "eee")]]))]
       (doall
        (for [week (-> calendar first)]
          ^{:key (str "week-" (first week))}
          [:div {:class "flex"}
           (doall
            (for [day week]
              ^{:key day}
              [:button {:class (class-names
                                "flex justify-center items-center"
                                "border rounded"
                                "w-10 h-10 text-sm"
                                "transition"
                                "hover:bg-neutral-600 focus:border-neutral-200 focus:outline-none"
                                (when (isSelected day) "font-bold bg-neutral-700")
                                (if (-> day d/isToday) "border-dashed border-neutral-300" "border-transparent"))
                        :on-click (fn []
                                    (select day true)
                                    (on-select day))}
               (d/getDate day)]))]))]]]))
