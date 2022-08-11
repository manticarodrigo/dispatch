(ns app.config
  (:require-macros [adzerk.env]))

(def debug?
  ^boolean goog.DEBUG)

(declare GOOGLE_MAPS_API_KEY)

(adzerk.env/def
  GOOGLE_MAPS_API_KEY (System/getenv "GOOGLE_MAPS_API_KEY"))

(def env
  {:google-maps-api-key GOOGLE_MAPS_API_KEY})
