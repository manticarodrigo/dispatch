(ns app.components.radio-group
  (:require ["@headlessui/react" :refer (RadioGroup)]
            [app.utils.string :refer (class-names)]))

(def ^:private Label (.-Label RadioGroup))
(def ^:private Option (.-Option RadioGroup))

(defn radio-group [{sr-label :sr-label
                    label :label
                    value :value
                    options :options
                    class :class
                    options-class :options-class
                    on-change :on-change}]
  [:div {:class class}
   [:> RadioGroup
    {:value value
     :on-change on-change}
    [:> Label {:class (when sr-label "sr-only")} (or sr-label label)]

    [:div {:class (class-names options-class "flex")}
     (doall
      (for [[idx option] (map-indexed vector options)]
        (let [first? (= idx 0)
              last? (= (+ 1 idx) (count options))]
          [:> Option
           {:key (:key option)
            :value (:key option)
            :class (fn [props]
                     (let [{active :active checked :checked} (js->clj props :keywordize-keys true)]
                       (class-names
                        "cursor-pointer outline-0 ring-white"
                        "border-y border-white/[0.4]  py-1 px-2 font-medium"
                        (if active "relative z-1 ring-1" "hover:relative hover:z-1 hover:ring-1")
                        (if first? "rounded-l border-l" "")
                        (if last? "rounded-r border-r" "")
                        (if (or checked active)
                          (class-names
                           "text-white"
                           (if checked "bg-white/[0.4]" "bg-white/[0.2]")
                           (if active "" ""))
                          "text-white/[0.8] bg-white/[0.2]"))))}
           [:> Label {:as "p"} (:label option)]])))]]])
