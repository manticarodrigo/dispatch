(ns tests
  (:require [cljs.test :as t]
            [promesa.core :as p]
            [tests.user :as user]))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println "-" (-> m :var meta :name)))

(defn test-ns-hook []
  (p/do
    (user/register)
    (user/login)
    (user/delete)))

(defn run-tests []
  (println "starting tests...")
  (println)
  (test-ns-hook))

(comment
  (run-tests))
