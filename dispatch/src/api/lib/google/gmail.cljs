(ns api.lib.google.gmail
  (:require ["googleapis" :refer (google)]
            ["nodemailer" :as nodemailer]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]))

(def credentials (->> (inline "google/gmail/credentials.json") (.parse js/JSON) ->clj))
(def token (->> (inline "google/gmail/token.json") (.parse js/JSON) ->clj))

(def auth
  {:type "OAuth2"
   :user "notifications@ambito.app"
   :clientId (-> credentials :web :client_id)
   :clientSecret (-> credentials :web :client_secret)
   :refreshToken (-> token :refresh_token)})

(def ^js auth-client
  (-> (-> google .-auth .-OAuth2)
      (new
       (-> credentials :web :client_id)
       (-> credentials :web :client_secret)
       (-> credentials :web :redirect_uris first))))

(.setCredentials auth-client (->js token))

(defn send-mail [options]
  (p/let [access-token (.getAccessToken auth-client)
          ^js transport (.createTransport
                         nodemailer
                         (->js {:service "gmail"
                                :auth (merge auth {:accessToken access-token})}))
          mail-options (merge options {:from "Ambito Dispatch <notifications@ambito.app>"})]
    (.sendMail transport (->js mail-options))))
