(ns api.util.test
  (:require ["supertest" :as request]
            [cljs-bean.core :refer (->js)]))


(defn send [body]
  (-> (request "http://localhost:3000")
      (.post "/")
      (.send (->js body))))
