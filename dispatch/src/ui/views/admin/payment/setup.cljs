(ns ui.views.admin.payment.setup
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.stripe :refer (stripe-elements)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.forms.payment :refer (payment-form)]))

(def FETCH_SETUP_INTENT (gql (inline "queries/payment/setup-intent.graphql")))

(defn view []
  (let [{:keys [data loading]} (use-query FETCH_SETUP_INTENT {})]
    [:div {:class padding}
     [title {:title (tr [:view.payment/title])
             :subtitle (tr [:view.payment/subtitle])}]
     (if loading
       (tr [:misc/loading])
       [stripe-elements {:secret (:paymentSetupIntent data)}
        [payment-form]])]))
