(ns ui.components.table
  (:require [react :refer (useMemo)]
            ["@tanstack/react-table"
             :refer [flexRender
                     getCoreRowModel
                     useReactTable]]
            [cljs-bean.core :refer (->js)]))

(defn table [{:keys [state data columns enable-row-selection on-row-selection-change]
              :or {data [] columns []}}]
  (let [_data (useMemo #(->js data) (array data))
        _columns (useMemo #(->js columns) (array columns))
        ^js instance (useReactTable
                      #js{:state state
                          :data _data
                          :columns _columns
                          :enableRowSelection enable-row-selection
                          :onRowSelectionChange on-row-selection-change
                          :getCoreRowModel (getCoreRowModel)})
        ^js header-groups (.getHeaderGroups instance)
        ^js rows (.-rows (.getRowModel instance))]
    [:table {:class "w-full"}
     [:thead {:class "border-b border-neutral-700"}
      (for [^js header-group header-groups]
        [:tr {:key (.-id header-group)}
         (for [^js header (.-headers header-group)]
           [:th {:key (.-id header)
                 :class "py-2 px-4 text-sm text-left font-normal whitespace-nowrap"}
            (if (.-isPlaceholder header)
              nil
              (flexRender
               (-> header .-column .-columnDef .-header)
               (.getContext header)))])])]
     [:tbody
      (for [^js row rows]
        [:tr {:key (.-id row)
              :class "border-b border-neutral-800"}
         (for [^js cell (.getVisibleCells row)]
           [:td {:key (.-id cell)
                 :class "py-2 px-4 text-xs whitespace-nowrap"}
            (flexRender
             (-> cell .-column .-columnDef .-cell)
             (.getContext cell))])])]]))
