(ns ui.lib.stripe
  (:require ["@stripe/stripe-js" :refer (loadStripe)]
            ["@stripe/react-stripe-js" :refer (Elements PaymentElement useStripe useElements)]
            [cljs-bean.core :refer (->js ->clj)]
            [ui.subs :refer (listen)]
            [ui.config :as config]))

(def stripe-promise
  (loadStripe config/STRIPE_PUBLIC_KEY))

(defn stripe-elements [{:keys [secret]} & children]
  (let [language (listen [:language])]
    [:> Elements {:stripe stripe-promise
                  :options {:clientSecret secret
                            :locale language
                            :appearance
                            {:theme "stripe"
                             :variables
                             {:fontFamily "Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica Neue, Arial, Noto Sans, sans-serif"
                              :fontSizeBase "16px"
                              :borderRadius "0.25rem"
                              :colorPrimary "#fafafa"
                              :colorText "#fafafa"
                              :colorBackground "#171717"}}}}
     (into [:<>] children)]))

(defn stripe-payment-element []
  [:> PaymentElement])

(defn use-confirm-setup []
  (let [^js stripe (useStripe)
        f (some-> stripe .-confirmSetup)]
    (when f
      (fn [params]
        (-> (f (->js params))
            (.then ->clj))))))

(defn use-elements []
  (useElements))
