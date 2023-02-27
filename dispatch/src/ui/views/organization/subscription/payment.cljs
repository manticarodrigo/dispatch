(ns ui.views.organization.subscription.payment
  (:require [react-feather :rename {CreditCard CreditCardIcon
                                    CheckCircle CheckIcon
                                    Trash TrashIcon}]
            [reagent.core :as r]
            [shadow.resource :refer (inline)]
            [common.utils.date :refer (parse-date)]
            [ui.lib.apollo :refer (gql use-query use-mutation)]
            [ui.lib.router :refer (use-search-params)]
            [ui.lib.stripe :refer (stripe-elements use-setup-intent-status)]
            [ui.utils.date :as d]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.callout :refer (callout)]
            [ui.components.article :refer (article)]
            [ui.components.modal :refer (modal)]
            [ui.components.inputs.button :refer (button)]
            [ui.components.icons.spinner :refer (spinner)]
            [ui.components.forms.payment :refer (payment-form)]))

(def FETCH_SETUP_INTENT (gql (inline "queries/stripe/setup-intent.graphql")))
(def FETCH_PAYMENT_METHODS (gql (inline "queries/stripe/payment-methods.graphql")))
(def DETACH_PAYMENT_METHOD (gql (inline "mutations/stripe/detach-payment-method.graphql")))

(defn return-url-status [secret]
  (let [status (use-setup-intent-status secret)]
    (if status
      (case status
        "succeeded" (tr [:view.subscription.payment/succeeded])
        "processing" (tr [:view.subscription.payment/processing])
        (tr [:view.subscription.payment/failed]))
      (str (tr [:misc/loading]) "..."))))

(defn card-detail [id {:keys [brand exp_month exp_year last4 created]}]
  (let [[detach status] (use-mutation DETACH_PAYMENT_METHOD {:refetchQueries [{:query FETCH_PAYMENT_METHODS}]})
        {:keys [loading]} status]
    [article {:icon CreditCardIcon
              :title [:<> [:span {:class "capitalize"} brand] " ending in " last4]
              :subtitle (str "Expires " exp_month "/" exp_year)
              :detail [:div {:class "flex items-center h-full"}
                       [:div {:class "flex flex-col items-end"}
                        [:div {:class "flex items-center text-xs text-neutral-400 capitalize"}
                         [:> CheckIcon {:class "mr-1 w-3 h-3 text-green-500"}]
                         "Added"]
                        [:div {:class "flex items-center text-xs text-left text-neutral-200"}
                         (-> created parse-date (d/format "dd/MM/yyyy"))]]
                       [button
                        {:type "button"
                         :class "ml-2"
                         :aria-label (tr [:view.subscription.payment/delete-payment-method])
                         :label (if loading [spinner {:class "w-4 h-4"}] [:> TrashIcon {:class "w-4 h-4"}])
                         :on-click #(detach {:variables {:paymentMethodId id}})}]]}]))

(defn payment-methods-list [payment-methods]
  (if (seq payment-methods)
    [:ul
     (for [{:keys [id card]} payment-methods]
       ^{:key id}
       [:li {:class "mb-2"} [card-detail id card]])]
    (tr [:misc/empty-search])))

(defn setup-intent []
  (let [{:keys [data loading]} (use-query FETCH_SETUP_INTENT {})
        secret (some-> data :stripe :setupIntent :client_secret)]
    (if loading
      (str (tr [:misc/loading]) "...")
      [stripe-elements {:secret secret}
       [payment-form]])))

(defn setup-modal []
  (let [!add-card? (r/atom false)]
    (fn []
      [:<>
       [button {:class "mt-4 w-full"
                :label (tr [:view.subscription.payment/add-payment-method])
                :on-click #(reset! !add-card? true)}]
       [modal {:show @!add-card?
               :title (tr [:view.subscription.payment/add-payment-method])
               :on-close #(reset! !add-card? false)}
        [setup-intent]]])))

(defn view []
  (let [[search-params] (use-search-params)
        secret (:setup_intent_client_secret search-params)
        {:keys [data loading]} (use-query FETCH_PAYMENT_METHODS {})
        payment-methods (some-> data :stripe :paymentMethods :data)]
    [:div {:class padding}
     [title {:title (tr [:view.subscription.payment/title])
             :subtitle (tr [:view.subscription.payment/subtitle])}]
     (when secret
       [stripe-elements {:secret secret}
        [:div {:class "mb-4"}
         [callout "success" [return-url-status secret]]]])
     (if loading
       (str (tr [:misc/loading]) "...")
       [payment-methods-list payment-methods])
     [setup-modal]]))
