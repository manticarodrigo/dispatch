(ns ui.lib.datadog.rum
  (:require ["@datadog/browser-rum" :refer (datadogRum)]
            [cljs-bean.core :refer (->js)]
            [common.config :as config]))

(when config/STAGE
  (.init
   datadogRum
   (->js {:applicationId "4afda6c4-4568-4cb5-8ceb-35a7f4267572"
          :clientToken "pube6b39edae8d33b6565be537b13f40e1d"
          :site "datadoghq.com"
          :service "ui"
          :env config/STAGE
          :version config/VERSION
          :sampleRate 100
          :sessionReplaySampleRate 20
          :trackInteractions true
          :trackResources true
          :trackLongTasks true
          :allowedTracingOrigins #js[config/API_URL]})))

