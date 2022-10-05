(ns deps
  (:require [nbb.classpath :refer [add-classpath]]))

(add-classpath "./src")
(add-classpath "./migrations")
