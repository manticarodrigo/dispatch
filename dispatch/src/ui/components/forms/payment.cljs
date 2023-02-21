(ns ui.components.forms.payment
  (:require [reagent.core :as r]
            [promesa.core :as p]
            [ui.lib.router :refer (use-navigate use-window-location)]
            [ui.lib.stripe :refer (use-confirm-setup use-elements stripe-payment-element)]
            [ui.components.inputs.submit-button :refer (submit-button)]
            [ui.components.errors :refer (error)]))


(defn payment-form []
  (let [!error (r/atom nil)]
    (fn []
      (let [confirm (use-confirm-setup)
            elements (use-elements)
            navigate (use-navigate)
            location (use-window-location)
            return-path "/payments"
            return-url (str (:protocol location) "//" (:host location) return-path)]
        [:form {:class "flex flex-col"
                :on-submit
                (fn [e]
                  (.preventDefault e)
                  (p/let [{:keys [error]} (confirm {:elements elements
                                                    :redirect "if_required"
                                                    :confirmParams
                                                    {:return_url return-url}})]
                    (if error
                      (reset! !error (:message error))
                      (navigate "/payments"))))}
         [stripe-payment-element]
         [submit-button {:loading (or
                                   (nil? confirm)
                                   (nil? elements))}]
         [error @!error]]))))
