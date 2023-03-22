(ns ui.components.table
  (:require ["react" :refer (useState useMemo)]
            ["react-feather" :rename {ChevronUp ChevronUpIcon
                                      ChevronDown ChevronDownIcon
                                      Info InfoIcon}]
            ["@tanstack/react-table" :refer (flexRender
                                             getCoreRowModel
                                             getFilteredRowModel
                                             getSortedRowModel
                                             useReactTable)]
            ["@tanstack/match-sorter-utils" :refer (rankItem)]
            [reagent.core :as r]
            [cljs-bean.core :refer (->js)]
            [ui.components.inputs.tooltip :refer (tooltip)]))

(defn data->selected-row-ids [data selection]
  (->> data
       (map-indexed vector)
       (filter
        (fn [[idx]]
          (= true (aget selection idx))))
       (mapv (fn [[_ {:keys [id]}]] id))))

(defn fuzzy-filter [^js row column-id value add-meta]
  (let [item-rank (rankItem (.getValue row column-id) value)]
    (add-meta #js{:itemRank item-rank})
    (.-passed item-rank)))

(defn tooltip-header [{:keys [label content required]}]
  (fn []
    (r/as-element
     [:span {:class "flex items-center"}
      [:span
       {:class
        (when required
          "after:content-['*'] after:ml-0.5 after:text-red-500")}
       label]
      [tooltip
       [:> InfoIcon {:class "ml-2 w-4 h-4"}]
       [:span content]]])))

(defn table [{:keys [state
                     data
                     columns
                     search-term
                     set-search-term
                     enable-row-selection
                     on-row-selection-change]
              :or {data [] columns []}}]
  (let [[sorting set-sorting] (useState #js[])
        _data (useMemo #(->js (or data [])) (array data))
        _columns (useMemo #(->js columns) (array columns))
        ^js instance (useReactTable
                      #js{:state (when state (js/Object.assign state #js{:sorting sorting
                                                                         :globalFilter search-term}))
                          :data _data
                          :columns _columns
                          :globalFilterFn fuzzy-filter
                          :onGlobalFilterChange set-search-term
                          :onSortingChange set-sorting
                          :enableRowSelection enable-row-selection
                          :onRowSelectionChange on-row-selection-change
                          :getCoreRowModel (getCoreRowModel)
                          :getFilteredRowModel (getFilteredRowModel)
                          :getSortedRowModel (getSortedRowModel)})
        ^js header-groups (.getHeaderGroups instance)
        ^js rows (.-rows (.getRowModel instance))]
    [:table {:class "w-full"}
     [:thead {:class "border-b border-neutral-700"}
      (for [^js header-group header-groups]
        [:tr {:key (.-id header-group)}
         (for [^js header (.-headers header-group)]
           [:th {:key (.-id header)
                 :class (str
                         "py-2 px-4 text-sm text-left font-normal whitespace-nowrap"
                         (when (and
                                (> (count data) 1)
                                (-> header .-column .getCanSort))
                           " cursor-pointer hover:bg-neutral-800"))
                 :on-click (-> header .-column .getToggleSortingHandler)}
            (if (.-isPlaceholder header)
              nil
              (flexRender
               (-> header .-column .-columnDef .-header)
               (.getContext header)))
            (case
             (-> header .-column .getIsSorted)
              "asc" [:> ChevronUpIcon {:class "inline w-4 h-4 ml-1 text-neutral-200"}]
              "desc" [:> ChevronDownIcon {:class "inline w-4 h-4 ml-1 text-neutral-200"}]
              "")])])]
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
