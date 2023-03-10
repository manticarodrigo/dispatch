(ns ui.components.tables.plan
  (:require [react :refer (useState)]
            [clojure.string :as s]
            [reagent.core :as r]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.router :refer (link use-params)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.utils.date :as d]
            [ui.utils.i18n :refer (tr)]
            [ui.components.table :refer (table)]
            [ui.components.inputs.button :refer (button button-class)]
            [ui.components.inputs.loading-button :refer (loading-button)]
            [ui.components.modal :refer (modal)]
            [ui.components.tables.visit :refer (visit-table)]
            [ui.components.inputs.combobox :refer (combobox)]))

(def FETCH_ORGANIZATION_PLAN (gql (inline "queries/user/organization/fetch-plan.graphql")))
(def CREATE_PLAN (gql (inline "mutations/plan/create.graphql")))

(defn checkbox [{:keys [checked disabled on-change]}]
  [:input {:type "checkbox"
           :checked checked
           :disabled disabled
           :on-change on-change}])

(defn visit-cell [visits]
  (let [!show-modal (r/atom false)]
    (fn []
      (if (seq visits)
        [:div {:class "flex justify-between items-center"}
         [:span (count visits)]
         [button {:label (s/capitalize (tr [:verb/show]))
                  :class "ml-2"
                  :on-click #(reset! !show-modal true)}]
         [modal {:show @!show-modal
                 :title (tr [:table.plan/visits])
                 :on-close #(reset! !show-modal false)}
          [:div {:class "overflow-auto w-full h-full"}
           [visit-table {:visits visits}]]]]
        "0"))))

(defn flexible-visit-cell [route]
  (let [[show-modal set-show-modal] (useState false)
        {plan-id :plan} (use-params)
        query (use-query FETCH_ORGANIZATION_PLAN {:variables {:planId plan-id}
                                                  :fetchPolicy "cache-first"})
        [create-plan create-plan-status] (use-mutation CREATE_PLAN {})
        loading (or (-> query :loading)
                    (-> create-plan-status :loading))
        {:keys [endAt depot]} (-> query :data :user :organization :plan)
        first-window? #(and
                        (> (count (-> % :shipment :windows)) 1)
                        (d/isBefore (-> % :arrival js/Date. .getTime)
                                    (-> % :shipment :windows second :start js/Date. .getTime)))
        visits (filter
                #(and
                  (> (count (-> % :shipment :windows)) 1)
                  (d/isBefore (-> % :arrival js/Date. .getTime)
                              (-> % :shipment :windows second :start js/Date. .getTime)))
                (-> route :visits))
        adaptable-visits (drop-while (comp not first-window?) (-> route :visits))]
    (if (seq visits)
      [:div {:class "flex justify-between items-center"}
       [:span (count visits)]
       [button {:label (s/capitalize (tr [:verb/show]))
                :class "ml-2"
                :on-click #(set-show-modal true)}]
       [modal {:show show-modal
               :title (tr [:table.plan/visits])
               :on-close #(set-show-modal false)}
        [:div {:class "p-4"}
         [loading-button
          {:loading loading
           :label "Crear plan de respaldo"
           :on-click #(do
                        (create-plan
                         {:variables
                          {:depotId (-> depot :id)
                           :startAt (-> adaptable-visits first :arrival js/Date.)
                           :endAt endAt
                           :breaks []
                           :vehicleIds [(-> route :vehicle :id)]
                           :shipmentIds (map (fn [visit]
                                               (-> visit :shipment :id)) adaptable-visits)}}))}]]
        [:div {:class "overflow-auto w-full h-full"}
         [visit-table {:visits visits}]]]]
      "0")))

(defn ratio-detail [num denom unit]
  (let [perc (-> num (/ denom) (* 100) (.toFixed 2))]
    [:div
     [:div [:span num] [:i {:class "font-thin"} " / " denom unit]]
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

(defn get-columns [agents !selected-agents]
  [{:id "select"
    :header (fn [^js info]
              (r/as-element
               [checkbox
                {:checked (-> info .-table .getIsAllRowsSelected)
                 :on-change (-> info .-table .getToggleAllRowsSelectedHandler)}]))
    :cell (fn [^js info]
            (r/as-element
             [checkbox
              {:checked (-> info .-row .getIsSelected)
               :disabled (not (-> info .-row .getCanSelect))
               :on-change (-> info .-row .getToggleSelectedHandler)}]))}
   {:id "task"
    :header (tr [:table.plan/task])
    :cell (fn [^js info]
            (let [^js task (-> info .-row .-original .-vehicle .-tasks first)]
              (if task
                (r/as-element
                 [link {:to (str "../tasks/" (.. task -id))
                        :class button-class}
                  (tr [:table.plan/go-to-task])])
                "")))}
   {:id "agent"
    :header (tr [:table.plan/agent])
    :cell (fn [^js info]
            (let [row-index (-> info .-row .-index)
                  ^js task (-> info .-row .-original .-vehicle .-tasks first)
                  agent-id (get @!selected-agents row-index)]
              (if task
                (->> agents (filter #(= agent-id (:id %))) first :name)
                (r/as-element
                 [combobox {:aria-label (tr [:table.plan/agent])
                            :value agent-id
                            :options agents
                            :option-to-label :name
                            :option-to-value :id
                            :class "min-w-[200px]"
                            :on-change #(swap! !selected-agents assoc row-index %)}]))))}
   {:id "vehicle"
    :header (tr [:table.plan/vehicle])
    :accessorFn #(.. ^js % -vehicle -name)}
   {:id "visits"
    :header (tr [:table.plan/visits])
    :accessorFn #(count (.. ^js % -visits))
    :cell (fn [^js info]
            (r/as-element [visit-cell (-> info .-row .-original .-visits ->clj)]))}
   {:id "flexibleVisits"
    :header (tr [:table.plan/flexible-visits])
    :cell (fn [^js info]
            (let [route (-> info .-row .-original ->clj)]
              (r/as-element [flexible-visit-cell route])))}
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
            (let [fmt #(-> % (/ 100000) (.toFixed 2))
                  val (.getValue info)
                  capacity (.. info -row -original -vehicle -capacities -volume)]
              (r/as-element
               [ratio-detail (fmt val) (fmt capacity) "m³"])))}
   {:id "weight"
    :header (tr [:table.plan/weight])
    :accessorFn #(.. ^js % -weight)
    :cell (fn [^js info]
            (let [fmt #(-> % (/ 1000) (.toFixed 2))
                  val (.getValue info)
                  capacity (.. info -row -original -vehicle -capacities -weight)]
              (r/as-element
               [ratio-detail (fmt val) (fmt capacity) "kg"])))}])

(defn plan-table [{:keys [result
                          agents
                          selected-rows
                          set-selected-rows
                          !selected-agents]}]
  [table {:state #js{:rowSelection selected-rows
                     :agentSelection @!selected-agents}
          :data result
          :columns (get-columns agents !selected-agents)
          :enable-row-selection #(and (some-> ^js % .-original .-visits seq)
                                      (some-> ^js % .-original .-vehicle .-tasks empty?))
          :on-row-selection-change set-selected-rows}])
