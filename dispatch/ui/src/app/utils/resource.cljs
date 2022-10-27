(ns app.utils.resource
  (:require [shadow.resource :as rc]))

(defn inline [resource-path]
  (rc/inline (str "resources/" resource-path)))
