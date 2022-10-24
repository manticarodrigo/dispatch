(ns app
  (:require [lib.apollo :refer (handler)]))

#js {:handler handler}
