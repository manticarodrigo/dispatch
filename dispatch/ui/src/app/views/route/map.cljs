(ns app.views.route.map
  (:require [app.hooks.use-route :refer (use-route-context)]
            [app.utils.string :refer (class-names)]))

(defn gmap [class]
  (let [{!el :ref} (use-route-context)]
    [:div
     {:ref (fn [el] (reset! !el el))
      :class (class-names class "w-full lg:h-full")}]))
