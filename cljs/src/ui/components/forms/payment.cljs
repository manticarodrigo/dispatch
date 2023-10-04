(ns ui.components.forms.payment
  (:require [reagent.core :as r]
            [promesa.core :as p]
            [ui.lib.router :refer (use-window-location)]
            [ui.lib.stripe :refer (use-confirm-setup use-elements stripe-payment-element)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (error)]))


(defn payment-form []
  (let [!loading? (r/atom false)
        !error (r/atom nil)]
    (fn []
      (let [confirm (use-confirm-setup)
            elements (use-elements)
            location (use-window-location)
            return-path "/organization/subscription/payment"
            return-url (str (:protocol location) "//" (:host location) return-path)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (reset! !loading? true)
                  (p/let [{:keys [error]} (confirm {:elements elements
                                                    :confirmParams
                                                    {:return_url return-url}})]
                    (reset! !error (some-> error :message))
                    (reset! !loading? false)))}
         [stripe-payment-element]
         [submit-button {:loading (or
                                   @!loading?
                                   (nil? confirm)
                                   (nil? elements))}]
         [error @!error]]))))
