(ns ui.components.inputs.date
  (:require ["react-feather" :rename {ChevronLeft ChevronLeftIcon
                                      ChevronRight ChevronRightIcon
                                      Calendar CalendarIcon}]
            ["use-lilius" :refer (useLilius)]
            [cljs-bean.core :refer (->clj)]
            [headlessui-reagent.core :as ui]
            [ui.lib.floating :refer (use-floating)]
            [ui.utils.date :as d]
            [ui.utils.string :refer (class-names)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.menu :refer (menu-class)]
            [ui.components.inputs.button :refer (button)]))

(defn date-select [{:keys [class label required placeholder value on-select]}]
  (let [{:keys [x y reference floating strategy]} (use-floating)
        {:keys [calendar
                isSelected
                select
                viewing
                viewNextMonth
                viewPreviousMonth]}
        (->clj
         (useLilius (if value #js{:selected #js[value]} #js{})))]
    [ui/popover {:class (class-names class "relative")}
     [ui/popover-button {:ref reference :class "w-full text-left"}
      [input {:icon CalendarIcon
              :label label
              :aria-label placeholder
              :placeholder placeholder
              :value (if value (d/format value "dd/MM/yyyy") "")
              :required required
              :on-change #()}]]

     [ui/popover-panel {:ref floating
                        :style {:position strategy
                                :top (or y 0)
                                :left (or x 0)}
                        :class (class-names menu-class "z-10")}
      [:div {:class "p-2"}
       [:div {:class "flex items-center"}
        [button {:type "button"
                 :aria-label (tr [:calendar/previous-month])
                 :label [:> ChevronLeftIcon {:class "w-4 h-4"}]
                 :class "!p-1"
                 :on-click viewPreviousMonth}]
        [:p {:class "flex-1 text-sm text-center capitalize"}
         (d/format viewing "MMMM yyyy")]
        [button {:type "button"
                 :aria-label (tr [:calendar/next-month])
                 :label [:> ChevronRightIcon {:class "w-4 h-4"}]
                 :class "!p-1"
                 :on-click viewNextMonth}]]
       [:div {:class "flex"}
        (doall
         (for [day (-> calendar first first)]
           ^{:key day}
           [:div {:class "flex justify-center items-center w-8 h-8"}
            [:span {:class "text-sm text-neutral-300 font-light capitalize"}
             (d/format day "eee")]]))]
       (doall
        (for [week (-> calendar first)]
          ^{:key (str "week-" (first week))}
          [:div {:class "flex"}
           (doall
            (for [day week]
              ^{:key day}
              [:button {:type "button"
                        :class (class-names
                                "flex justify-center items-center"
                                "border rounded"
                                "w-8 h-8 text-sm"
                                "transition"
                                "hover:bg-neutral-600 focus:border-neutral-200 focus:outline-none"
                                (when (isSelected day) "font-bold bg-neutral-700")
                                (if (-> day d/isToday) "border-dashed border-neutral-300" "border-transparent"))
                        :on-click (fn []
                                    (select day true)
                                    (on-select day))}
               (d/getDate day)]))]))]]]))
