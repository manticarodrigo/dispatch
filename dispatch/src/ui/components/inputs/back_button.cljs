(ns ui.components.inputs.back-button
  (:require [react-feather :rename {ArrowLeft ArrowLeftIcon}]
            [ui.lib.router :refer (use-navigate)]))

(defn back-button []
  (let [navigate (use-navigate)]
    [:button {:on-click #(navigate -1)}
     [:> ArrowLeftIcon {:class "w-4 h-4"}]]))
