(ns api.lib.google.gmail
  (:require
   ["resend" :refer (Resend)]
   [common.config :as config]
  ;;  ["googleapis" :refer (google)]
  ;;  ["nodemailer" :as nodemailer]
  ;;  [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj ->js)]
  ;;  [promesa.core :as p]
   ))

;; (def credentials (->> (inline "google/gmail/credentials.json") (.parse js/JSON) ->clj))
;; (def token (->> (inline "google/gmail/token.json") (.parse js/JSON) ->clj))

;; (def auth
;;   {:type "OAuth2"
;;    :user "notifications@ambito.app"
;;    :clientId (-> credentials :web :client_id)
;;    :clientSecret (-> credentials :web :client_secret)
;;    :refreshToken (-> token :refresh_token)})

;; (def ^js auth-client
;;   (-> (-> google .-auth .-OAuth2)
;;       (new
;;        (-> credentials :web :client_id)
;;        (-> credentials :web :client_secret)
;;        (-> credentials :web :redirect_uris first))))

;; (.setCredentials auth-client (->js token))

;; (defn send-mail [options]
;;   (p/let [access-token (.getAccessToken auth-client)
;;           ^js transport (.createTransport
;;                          nodemailer
;;                          (->js {:service "gmail"
;;                                 :auth (merge auth {:accessToken access-token})}))
;;           mail-options (merge options {:from "Ambito Dispatch <notifications@ambito.app>"})]
;;     (.sendMail transport (->js mail-options))))


;; const resend = new Resend('re_LZJAod21_GWTvJxqUst6DfbDKPysbnZn9');

;; resend.emails.send({
;;   from: 'onboarding@resend.dev',
;;   to: 'manticarodrigo@gmail.com',
;;   subject: 'Hello World',
;;   html: '<p>Congrats on sending your <strong>first email</strong>!</p>'
;; });

;; {:to email
;;  :subject "Verification code"
;;  :text text
;;  :html (render-to-string [:p text])
;;  :textEncoding "base64"}
(defn send-mail [options]
  (let [^js resend (Resend. config/RESEND_API_KEY)]
    (-> resend .-emails (.send (->js (merge options {:from "Dispatch Notifications <updates@ambito.dev>"}))))))
