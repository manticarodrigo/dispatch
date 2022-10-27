(ns ui.components.generic.radio-group
  (:require [clojure.string :as s]
            ["@headlessui/react" :refer (RadioGroup)]
            [ui.utils.string :refer (class-names)]
            [ui.components.generic.button :refer (button-class)]))

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
                        (s/replace button-class #"rounded" "")
                        "cursor-pointer"
                        "border-y py-1 px-2"
                        (if first? "rounded-l border-l" "")
                        (if last? "rounded-r border-r" "")
                        (if (or checked active)
                          (class-names
                           "text-neutral-50"
                           (if checked "bg-neutral-800" "bg-neutral-900")
                           (if active "" ""))
                          "text-neutral-50 bg-neutral-900")
                        )))}
           [:> Label {:as "p"} (:label option)]])))]]])
