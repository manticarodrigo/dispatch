(ns dev.local-server
  (:require [deps]
            [dev.local-repo]
            [dynamo-repo]
            [htmx]
            [express]))

(def htmx-config
  (htmx/make-htmx-config
   {:repo :local}))

(def express-config
  (express/make-express-config
   {:htmx-config htmx-config
    :static-files-root "src/dev"}))

(def express-app
  (express/create-app express-config))

(express/start-server express-app)
(dev.local-repo/seed)
