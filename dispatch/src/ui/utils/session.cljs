(ns ui.utils.session
  (:require ["cookie" :refer (serialize parse)]
            ["@capacitor/core" :refer (Capacitor)]
            [goog.object :as gobj]
            [cljs-bean.core :refer (->js)]
            [re-frame.core :refer (dispatch-sync)]
            [ui.config :as config]))

(def cookie-name "sessionId")

(defn set-cookie! [str]
  (set! (.. js/document -cookie) str))

(defn get-session []
  (let [cookie-str (.. js/document -cookie)]
    (gobj/get (parse cookie-str) cookie-name)))

(defn create-session [session-id]
  (let [platform (.getPlatform Capacitor)
        web? (= platform "web")
        secure? (and web? config/SECURE_COOKIE)
        opts {:domain (.-hostname js/window.location)
              :maxAge (reduce * [60 60 24 7])
              :path "/"
              :secure secure?
              :sameSite (if secure? "none" "lax")}
        cookie-str (serialize cookie-name session-id (->js opts))]
    (set-cookie! cookie-str)
    (dispatch-sync [:session/set (get-session)])))

(defn remove-session []
  (let [opts {:domain (.-hostname js/window.location)
              :expires (js/Date. "1970")
              :path "/"}
        cookie-str (serialize cookie-name "" (->js opts))]
    (set-cookie! cookie-str)
    (dispatch-sync [:session/set nil])))

(defn get-session-request []
  {:headers
   {:authorization (or (get-session) "")}})
