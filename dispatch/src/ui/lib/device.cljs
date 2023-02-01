(ns ui.lib.device
  (:require ["@capacitor/device" :refer (Device)]
            [promesa.core :as p]
            [cljs-bean.core :refer (->clj)]
            [ui.lib.platform :refer (platform)]))

(defn get-id []
  (p/-> (.getId Device) (.-uuid)))

(defn get-info []
  (p/-> (.getInfo Device) ->clj))

(defn get-language-code []
  (p/-> (.getLanguageCode Device) (.-value)))

(defn get-battery-info []
  (.getBatteryInfo Device))

(defn get-device-info []
  (-> (p/all [(get-id)
              (get-language-code)
              (get-info)
              (if (= "web" platform)
                (p/resolved nil)
                (get-battery-info))])
      (.then (fn [[id language info battery]]
               {:id id
                :language language
                :info info
                :battery battery}))))
