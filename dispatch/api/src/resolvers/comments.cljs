(ns resolvers.comments
  (:require [promesa.core :as p]
            [repo]))

(defn get-comments
  "Retrieves a list of comments as JSON"
  [config post-id]
  (p/let [cmts (repo/get-comments config post-id)]
    (clj->js {:status 200 :body cmts})))
