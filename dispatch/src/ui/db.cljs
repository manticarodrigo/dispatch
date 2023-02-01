(ns ui.db
  (:require [ui.config :as config]
            [ui.utils.session :refer (get-session)]))

(def default-db {:session (get-session)
                 :language config/LANGUAGE})
