(ns ui.components.tables.route
  (:require ["react" :refer (useState)]
            ["react-feather" :rename {Edit CreateIcon
                                      ArrowRight ArrowRightIcon
                                      Eye ViewIcon}]
            [clojure.string :as s]
            [reagent.core :as r]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.router :refer (link)]
            [ui.lib.google.maps.polyline :refer (decode-polyline)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.table :refer (table)]
            [ui.components.map :refer (gmap)]
            [ui.components.modal :refer (modal)]
            [ui.components.tables.visit :refer (visit-table)]
            [ui.components.tables.columns.checkbox :refer (checkbox-column)]
            [ui.components.inputs.button :refer (button button-class)]
            [ui.components.inputs.combobox :refer (combobox)]
            [ui.components.inputs.radio-group :refer (radio-group)]))

(defn map-view [visits payload]
  (use-map-items
   false
   {:tasks [{:route {:path (-> payload :routePolyline :points decode-polyline)}}]
    :places (mapv
             #(or (-> % :depot)
                  (-> % :shipment :place))
             visits)}
   [visits payload])
  [gmap])

(defn visit-cell [{:keys [visits payload]}]
  (let [[show set-show] (useState false)
        [tab set-tab] (useState "table")]
    (if (seq visits)
      [:div {:class "flex justify-between items-center"}
       [button {:label [:div {:class "flex justify-center items-center"}
                        [:> ViewIcon {:class "mr-2 w-4 h-4"}]
                        (s/capitalize (tr [:verb/view]))
                        (when (> (count visits) 0)
                          [:<> " (" (count visits) ")"])]
                :class "w-full"
                :on-click #(set-show true)}]
       [modal {:show show
               :title (tr [:table.plan/visits])
               :on-close #(set-show false)}
        [:div {:class "flex flex-col items-start w-[80vw] h-[80vh]"}
         [:div {:class "p-4"}
          [radio-group {:options [{:label "Table" :key "table" :value "table"}
                                  {:label "Map" :key "map" :value "map"}]
                        :value tab
                        :on-change #(set-tab %)}]]
         [:div {:class "overflow-auto w-full h-full min-w-0 min-h-0"}
          (case tab
            "table" [visit-table {:visits visits}]
            "map" [map-view visits payload])]]]]
      "0")))

(defn ratio-detail [num denom unit]
  (let [perc (-> num (/ denom) (* 100) (.toFixed 2))]
    [:div
     [:div [:span (.toFixed num 2)] [:i {:class "font-thin"} " / " (.toFixed denom 2) unit]]
     [:div {:class "font-thin mt-1"} perc "%"]]))

(defn time-column [id label accessor-fn]
  {:id id
   :header label
   :accessorFn (fn [^js info]
                 (let [val (accessor-fn info)]
                   (if val
                     (-> val js/Date. .getTime)
                     nil)))
   :cell (fn [^js info]
           (let [val (.getValue info)]
             (if val
               (-> val js/Date. (d/format "hh:mmaaa"))
               "N/A")))})

(defn get-columns [agents selected-agents set-selected-agents on-create-task]
  [checkbox-column
   {:id "agent"
    :header (tr [:table.plan/agent])
    :accessorFn (fn [_ idx]
                  (let [agent-id (get selected-agents idx)]
                    (->> agents (filter #(= agent-id (:id %))) first :name)))
    :cell (fn [^js info]
            (let [row-index (-> info .-row .-index)
                  ^js task (-> info .-row .-original .-vehicle .-tasks first)
                  agent-id (get selected-agents row-index)]
              (if task
                (->> agents (filter #(= agent-id (:id %))) first :name)
                (r/as-element
                 [combobox {:aria-label (tr [:table.plan/agent])
                            :value agent-id
                            :options agents
                            :option-to-label :name
                            :option-to-value :id
                            :class "min-w-[200px]"
                            :on-change #(set-selected-agents (assoc selected-agents row-index %))}]))))}
   {:id "vehicle"
    :header (tr [:table.plan/vehicle])
    :accessorFn #(.. ^js % -vehicle -name)}
   {:id "visits"
    :header (tr [:table.plan/visits])
    :accessorFn #(count (.. ^js % -visits))
    :cell (fn [^js info]
            (r/as-element [visit-cell (-> info .-row .-original ->clj)]))}
   (time-column "start" (tr [:table.plan/start]) #(.-start ^js %))
   (time-column "end" (tr [:table.plan/end]) #(.-end ^js %))
   {:id "distance"
    :header (tr [:table.plan/distance])
    :accessorFn #(.. ^js % -meters)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)]
              (str (fmt val) "km")))}
   {:id "volume"
    :header (tr [:table.plan/volume])
    :accessorFn #(.. ^js % -volume)
    :cell (fn [^js info]
            (let [val (.getValue info)
                  capacity (.. info -row -original -vehicle -volume)]
              (r/as-element
               [ratio-detail val capacity "m³"])))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -weight)
    :cell (fn [^js info]
            (let [val (.getValue info)
                  capacity (.. info -row -original -vehicle -weight)]
              (r/as-element
               [ratio-detail val capacity "kg"])))}
   {:id "task"
    :header ""
    :cell (fn [^js info]
            (let [^js task (-> info .-row .-original .-vehicle .-tasks first)]
              (if task
                (r/as-element
                 [link {:to (str "../tasks/" (.. task -id))
                        :class (str
                                button-class
                                " w-full flex justify-center items-center")}
                  [:> ArrowRightIcon {:class "w-4 h-4 mr-2"}]
                  (tr [:table.plan/go-to-task])])
                (r/as-element
                 [button {:label [:div {:class "flex justify-center items-center"}
                                  [:> CreateIcon {:class "w-4 h-4 mr-2"}]
                                  (tr [:view.task.create/title])]
                          :class "w-full"
                          :on-click #(on-create-task (-> info .-row .-index))}]))))}])

(defn route-table [{:keys [result
                           agents
                           search-term
                           set-search-term
                           selected-rows
                           set-selected-rows
                           selected-agents
                           set-selected-agents
                           on-create-task]}]
  [table {:data result
          :columns (get-columns agents selected-agents set-selected-agents on-create-task)
          :state #js{:rowSelection selected-rows
                     :agentSelection selected-agents}
          :search-term search-term
          :set-search-term set-search-term
          :enable-row-selection #(let [v (->clj %)]
                                   (and (some-> v :original :visits seq)
                                        (some-> v :original :vehicle :tasks empty?)))
          :on-row-selection-change set-selected-rows}])
