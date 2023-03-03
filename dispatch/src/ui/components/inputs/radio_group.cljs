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

    [:div {:class (class-names options-class "rounded flex justify-between bg-neutral-800")}
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
                        "py-0.5 px-2"
                        "cursor-pointer"
                        "w-full text-center text-sm"
                        (when first? "rounded-l")
                        (when last? "rounded-r")
                        (if (or checked active)
                          (class-names
                           "text-neutral-50"
                           (when checked "rounded bg-neutral-700 focus:bg-neutral-700 shadow"))
                          "text-neutral-400 hover:text-neutral-50"))))}
           [:> Label {:as "p"} (:label option)]])))]]])
