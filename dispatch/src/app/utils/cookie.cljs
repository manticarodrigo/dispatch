(ns app.utils.cookie
  (:require ["cookie" :refer (serialize parse)]
            [goog.object :as gobj]
            [cljs-bean.core :refer (->js)]
            [app.config :as config]))

(def cookie-name "sessionId")

(defn set-cookie! [str]
  (set! (.. js/document -cookie) str))

(defn create-session [session-id]
  (let [opts {:domain (.-hostname js/window.location)
              :maxAge (reduce * [60 60 24 7])
              :path "/"
              :secure config/SECURE_COOKIE
              :sameSite (if config/SECURE_COOKIE "none" "lax")}
        cookie-str (serialize cookie-name session-id (->js opts))]
    (set-cookie! cookie-str)))

(defn get-session []
  (let [cookie-str (.. js/document -cookie)]
    (gobj/get (parse cookie-str) cookie-name)))

(defn remove-session []
  (let [opts {:domain (.-hostname js/window.location)
              :expires (js/Date. "1970")
              :path "/"}
        cookie-str (serialize cookie-name "" (->js opts))]
    (set-cookie! cookie-str)))
