(ns tests.util.request
  (:require ["supertest" :as request]
            [cljs-bean.core :refer (->js)]))


(defn send [body]
  (-> (request "http://localhost:3000")
      (.post "/")
      (.send (->js body))))
