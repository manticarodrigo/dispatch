(ns ui.components.table
  (:require ["@tanstack/react-table"
             :refer [flexRender
                     getCoreRowModel
                     useReactTable]]
            [cljs-bean.core :refer (->js)]))

(defn table [{:keys [data columns] :or {data [] columns []}}]
  (let [instance (useReactTable (->js {:data data
                                       :columns columns
                                       :getCoreRowModel (getCoreRowModel)}))
        header-groups (.getHeaderGroups instance)
        rows (.-rows (.getRowModel instance))]
    [:table {:class "w-full"}
     [:thead {:class "border-b border-neutral-700"}
      (for [header-group header-groups]
        [:tr {:key (.-id header-group)}
         (for [header (.-headers header-group)]
           [:th {:key (.-id header)
                 :class "py-2 px-4 text-sm text-left font-normal whitespace-nowrap"}
            (if (.-isPlaceholder header)
              nil
              (flexRender
               (-> header .-column .-columnDef .-header)
               (.getContext header)))])])]
     [:tbody
      (for [row rows]
        [:tr {:key (.-id row)
              :class "border-b border-neutral-800"}
         (for [cell (.getVisibleCells row)]
           [:td {:key (.-id cell)
                 :class "py-2 px-4 text-xs whitespace-nowrap"}
            (flexRender
             (-> cell .-column .-columnDef .-cell)
             (.getContext cell))])])]]))
