(ns ui.components.inputs.reorder
  (:require ["framer-motion" :refer (Reorder)]))

(defn reorder [{:keys [tuples
                       render-item
                       on-reorder
                       class]}]
  (let [draggable-item-ids (mapv first tuples)
        draggable-item-map (into {} (for [[item-id v] tuples]
                                      {item-id v}))]
    [:> (. Reorder -Group)
     {:axis "y"
      :class class
      :values draggable-item-ids
      :on-reorder #(on-reorder (mapv (fn [id] [id (get draggable-item-map id)]) %))}
     (doall
      (for [[idx [draggable-item-id v]] (map-indexed vector tuples)]
        ^{:key draggable-item-id}
        [:> (. Reorder -Item) {:value draggable-item-id :class "relative"}
         (render-item idx v)]))]))
