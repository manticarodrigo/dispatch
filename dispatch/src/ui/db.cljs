(ns ui.db
  (:require [ui.utils.session :refer (get-session)]))

(def default-db {:session (get-session)})
