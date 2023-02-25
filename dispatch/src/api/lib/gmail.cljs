(ns api.lib.gmail
  (:require ["googleapis" :refer (google)]
            ["nodemailer/lib/mail-composer" :as MailComposer]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]))

(def credentials (->> (inline "gmail/credentials.json") (.parse js/JSON)))
(def token (->> (inline "gmail/token.json") (.parse js/JSON)))

(defn get-service []
  (let [{:keys [client_id client_secret redirect_uris]} (->clj (.-web credentials))
        auth-class (-> google .-auth .-OAuth2)
        auth-client (new auth-class client_id client_secret (first redirect_uris))]
    (.setCredentials auth-client token)
    (.gmail google (->js {:version "v1" :auth auth-client}))))

(defn encode-message [message]
  (->
   (.from js/Buffer message)
   (.toString "base64")
   (.replace #"\+" "-")
   (.replace #"/" "_")
   (.replace #"=+$" "")))

(defn create-mail [options]
  (p/let [^js mail-composer (MailComposer. options)
          message (-> mail-composer .compile .build)]
    (encode-message message)))

(defn send-mail [options]
  (p/let [^js gmail (get-service)
          raw-msg (create-mail (->js options))
          ^js res (-> gmail .-users .-messages
                      (.send (->js {:userId "me"
                                    :resource {:raw raw-msg}})))]
    (.. res -data -id)))
