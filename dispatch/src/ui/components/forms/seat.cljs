(ns ui.components.forms.seat
  (:require [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql parse-anoms use-mutation)]
            [ui.lib.router :refer (use-navigate)]
            [ui.utils.error :refer (tr-error)]
            [ui.utils.string :refer (class-names)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.button :refer (button)]))

(def CREATE_SEAT (gql (inline "mutations/seat/create.graphql")))

(defn seat-form []
  (let [!state (r/atom {})
        !anoms (r/atom {})]
    (fn []
      (let [{:keys [name]} @!state
            [create status] (use-mutation CREATE_SEAT {})
            {:keys [loading]} status
            navigate (use-navigate)]
        [:form {:class "flex flex-col"
                :on-submit (fn [e]
                             (.preventDefault e)
                             (-> (create {:variables @!state})
                                 (.then (fn []
                                          (navigate "/admin/seats")))
                                 (.catch #(reset! !anoms (parse-anoms %)))))}
         [input {:id "name"
                 :label "Name"
                 :value name
                 :required true
                 :class "pb-4"
                 :on-text #(swap! !state assoc :name %)}]
         [button
          {:label (if loading
                    [:span {:class "flex justify-center items-center"}
                     [spinner {:class "mr-2 w-5 h-5"}] "Loading..."]
                    "Submit")
           :class (class-names "my-4 w-full" (when loading "cursor-progress"))
           :disabled loading}]
         (doall (for [anom @!anoms]
                  [:span {:key (:reason anom)
                          :class "my-2 p-2 rounded border border-red-300 text-sm text-red-100 bg-red-800"}
                   (tr-error anom)]))]))))
