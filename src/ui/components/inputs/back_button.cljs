(ns ui.components.inputs.back-button
  (:require ["next/navigation" :refer (useRouter)]
            ["react-feather" :rename {ArrowLeft ArrowLeftIcon}]
            [ui.lib.router :refer (use-pathname)]))

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
  (let [router (useRouter)
        pathname (use-pathname)]
    (when (empty? (filter #(= pathname %) root-paths))
      [:button {:class class
                :on-click #(.back router)}
       [:> ArrowLeftIcon {:class "w-4 h-4"}]])))
