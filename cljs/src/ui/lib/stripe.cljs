(ns ui.lib.stripe
  (:require ["react" :refer (useState useEffect)]
            ["@stripe/stripe-js" :refer (loadStripe)]
            ["@stripe/react-stripe-js" :refer (Elements PaymentElement useStripe useElements)]
            [cljs-bean.core :refer (->js ->clj)]
            [ui.subs :refer (listen)]
            [ui.config :as config]))

(def !stripe-promise (atom nil))

(defn get-stripe-promise []
  (or @!stripe-promise
      (reset! !stripe-promise (loadStripe config/STRIPE_PUBLIC_KEY))))

(defn stripe-elements [{:keys [secret]} & children]
  (let [language (listen [:language])]
    [:> Elements {:stripe (get-stripe-promise)
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

(defn use-elements []
  (useElements))

(defn use-confirm-setup []
  (let [^js stripe (useStripe)
        f (some-> stripe .-confirmSetup)]
    (when f
      (fn [params]
        (-> (f (->js params))
            (.then ->clj))))))

(defn use-setup-intent-status [secret]
  (let [^js stripe (useStripe)
        [intent set-intent] (useState nil)]
    (useEffect
     (fn []
       (when stripe
         (-> stripe
             (.retrieveSetupIntent secret)
             (.then set-intent)))
       #())
     #js[stripe])
    (some-> ^js intent .-setupIntent .-status)))
