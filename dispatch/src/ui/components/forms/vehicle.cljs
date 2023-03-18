(ns ui.components.forms.vehicle
  (:require [react :refer (useState)]
            [shadow.resource :refer (inline)]
            [reagent.core :as r]
            [ui.lib.apollo :refer (gql use-mutation parse-anoms)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (errors)]))

(def CREATE_VEHICLES (gql (inline "mutations/vehicle/create-vehicles.graphql")))

(def !anoms (r/atom nil))

(defn vehicle-form [{:keys [on-submit]}]
  (let [[state set-state] (useState nil)
        [create create-status] (use-mutation CREATE_VEHICLES {:refetchQueries ["OrganizationVehicles"]})
        {:keys [loading]} create-status
        {:keys [name weight volume]} state]
    [:form {:class "flex flex-col"
            :on-submit (fn [e]
                         (.preventDefault e)
                         (-> (create {:variables
                                      {:vehicles
                                       {:name (:name state)
                                        :weight (-> state :weight js/parseFloat)
                                        :volume (-> state :volume js/parseFloat)}}})
                             (.then on-submit)
                             (.catch #(reset! !anoms (parse-anoms %)))))}
     [input {:label (tr [:field/name])
             :value name
             :class "mb-4"
             :on-text #(set-state (assoc state :name %))}]
     [input {:label (tr [:field/weight])
             :value weight
             :class "mb-4"
             :on-text #(set-state (assoc state :weight %))}]
     [input {:label (tr [:field/volume])
             :value volume
             :class "mb-4"
             :on-text #(set-state (assoc state :volume %))}]
     [submit-button {:loading loading}]
     [errors @!anoms]]))
