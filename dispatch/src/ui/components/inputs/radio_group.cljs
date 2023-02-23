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
                        "py-1 px-2"
                        "cursor-pointer"
                        "border-y border-neutral-700"
                        "w-full text-center text-sm"
                        "transition"
                        "hover:text-neutral-50 hover:bg-neutral-700 focus:bg-neutral-700"
                        (when first? "rounded-l border-l")
                        (when last? "rounded-r border-r")
                        (if (or checked active)
                          (class-names
                           "text-neutral-50"
                           (when checked "border rounded border-neutral-500 bg-neutral-600 focus:bg-neutral-600"))
                          "text-neutral-400 bg-neutral-800"))))}
           [:> Label {:as "p"} (:label option)]])))]]])
