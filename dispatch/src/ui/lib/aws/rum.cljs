(ns ui.lib.aws.rum
  (:require
   ["aws-rum-web" :refer (AwsRum)]
   [ui.config :as config]))

(when config/RUM_MONITOR_ID
  (AwsRum.
   config/RUM_MONITOR_ID
   config/VERSION
   "us-east-1"
   #js{:sessionSampleRate 1
       :endpoint "https://dataplane.rum.us-east-1.amazonaws.com"
       :guestRoleArn config/RUM_GUEST_ROLE_ARN
       :identityPoolId config/RUM_IDENTITY_POOL_ID
       :telemetries #js["errors"
                        "performance"
                        #js["http" #js{:urlsToInclude #js[(re-pattern config/API_URL)]
                                       :addXRayTraceIdHeader true}]]
       :allowCookies true
       :enableXRay true}))
