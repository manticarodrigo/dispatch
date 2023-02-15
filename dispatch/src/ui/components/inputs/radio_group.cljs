(ns ui.components.inputs.radio-group
  (:require ["@headlessui/react" :refer (RadioGroup)]
            [cljs-bean.core :refer (->clj)]
            [ui.utils.string :refer (class-names)]))

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

    [:div {:class (class-names options-class "flex justify-between")}
     (doall
      (for [[idx option] (map-indexed vector options)]
        (let [first? (= idx 0)
              last? (= (+ 1 idx) (count options))]
          [:> Option
           {:key (:key option)
            :value (:key option)
            :class (fn [props]
                     (let [{active :active checked :checked} (->clj props)]
                       (class-names
                        "cursor-pointer"
                        "border-y border-neutral-800 py-1 px-2"
                        "w-full text-center text-sm"
                        (when first? "rounded-l border-l")
                        (when last? "rounded-r border-r")
                        (if (or checked active)
                          (class-names
                           "text-neutral-50"
                           "hover:border-neutral-500 focus:border-neutral-600"
                           (when checked "border rounded bg-neutral-700"))
                          "text-neutral-400 bg-neutral-900"))))}
           [:> Label {:as "p"} (:label option)]])))]]])
