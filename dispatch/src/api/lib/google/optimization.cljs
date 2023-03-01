(ns api.lib.google.optimization
  (:require ["googleapis" :refer (google)]
            ["axios" :as axios]
            ["csv-parse/sync" :refer (parse)]
            [shadow.resource :refer (inline)]
            [promesa.core :as p]
            [cljs-bean.core :refer (->clj ->js)]))

(def ^js auth-client
  (-> (-> google .-auth .-GoogleAuth)
      (new
       #js{:keyFile "resources/google/service_account.json"
           :scopes #js["https://www.googleapis.com/auth/cloud-platform"]})))

(defn optimize-tours [payload]
  (p/let [url "https://cloudoptimization.googleapis.com/v1/projects/dispatch-368818:optimizeTours"
          token (.getAccessToken auth-client)
          options (->js {:headers
                         {:Content-Type "application/json"
                          :Authorization (str "Bearer " token)}})]
    (-> (.post axios url payload options)
        (.then (fn [response]
                 (-> response .-data ->clj))))))

;; (-> (optimize-tours (inline "samples/tour.json"))
;;     (.then prn))


(def orders (parse
             (inline "samples/orders.csv")
             (->js {:columns ["order"
                              "reference"
                              "address"
                              "start1"
                              "end1"
                              "start2"
                              "end2"
                              "duration"
                              "volume"
                              "weight"
                              "latitude"
                              "longitude"]
                    :skip_empty_lines true})))

(def vehicles (parse
               (inline "samples/vehicles.csv")
               (->js {:columns ["description"
                                "maxWeight"
                                "maxVolume"
                                "maxItems"
                                "maxDuration"
                                "loadingTime"
                                "avgSpeed"
                                "breakStart"
                                "breakEnd"
                                "breakDuration"
                                "minBreakDuration"]
                      :skip_empty_lines true})))

(def warehouse (parse
                (inline "samples/warehouse.csv")
                (->js {:columns ["reference"
                                 "description"
                                 "address"
                                 "startTime"
                                 "endTime"
                                 "waitDuration"
                                 "latitude"
                                 "longitude"]
                       :skip_empty_lines true})))

(js/console.log warehouse)
