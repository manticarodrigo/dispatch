(ns ui.db
  (:require [ui.config :as config]
            [ui.utils.session :refer (get-session)]))

(def default-db {:session (get-session)
                 :locale {:region config/REGION
                          :language config/LANGUAGE}})
