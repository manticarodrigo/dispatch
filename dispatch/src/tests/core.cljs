(ns tests.core
  (:require [cljs.test :as t]))

(defmethod t/report [:cljs.test/default :begin-test-var] [m]
  (println "  " (-> m :var meta :name)))

(defmethod t/report [:cljs.test/default :pass] [m]
  (println "    " (t/testing-contexts-str) "(PASS)"))
