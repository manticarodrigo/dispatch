(ns app.config)

(def debug?
  ^boolean goog.DEBUG)

(goog-define GOOGLE_MAPS_API_KEY "")

(def env
  {:google-maps-api-key GOOGLE_MAPS_API_KEY})
