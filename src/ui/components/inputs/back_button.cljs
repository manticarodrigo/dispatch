(ns ui.components.inputs.back-button
  (:require ["react-feather" :rename {ArrowLeft ArrowLeftIcon}]
            [ui.lib.router :refer (use-pathname use-navigate)]))

(def root-paths ["/organization/analytics"
                 "/organization/agents"
                 "/organization/places"
                 "/organization/vehicles"
                 "/organization/shipments"
                 "/organization/plans"
                 "/organization/tasks"
                 "/agent/tasks"
                 "/agent/places"])

(defn back-button [{:keys [class]}]
  (let [navigate (use-navigate)
        pathname (use-pathname)]
    (when (empty? (filter #(= pathname %) root-paths))
      [:button {:class class
                :on-click #(navigate -1)}
       [:> ArrowLeftIcon {:class "w-4 h-4"}]])))
