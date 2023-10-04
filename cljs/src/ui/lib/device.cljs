(ns ui.lib.device
  (:require ["@capacitor/device" :refer (Device)]
            [promesa.core :as p]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.platform :refer (platform)]))

(defn get-id []
  (p/-> (.getId ^js Device) (.-uuid)))

(defn get-info []
  (p/-> (.getInfo ^js Device) ->clj))

(defn get-language-code []
  (p/-> (.getLanguageCode ^js Device) (.-value)))

(defn get-battery-info []
  (.getBatteryInfo ^js Device))

(defn get-ip-info []
  (-> (js/fetch "https://ipinfo.io/186.1.37.148?token=6ce43441822d69"
                #js{:method "GET"
                    :mode "cors"
                    :headers #js{"Content-Type" "application/json"}})
      (.then (fn [res]
               (.json res)))))

(defn get-country-code []
  (-> (get-ip-info)
      (.then (fn [^js info]
               (.-country info)))))

(defn get-device-info []
  (-> (p/all [(get-id)
              (get-language-code)
              (get-info)
              (get-country-code)
              (if (= "web" platform)
                (p/resolved nil)
                (get-battery-info))])
      (.then (fn [[id language info country battery]]
               {:id id
                :language language
                :country country
                :info info
                :battery battery}))))
