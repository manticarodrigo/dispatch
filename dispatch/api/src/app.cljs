(ns app
  (:require
   ["serverless-http$default" :as serverless]
   [config]
   [deps]
   [express]
   [htmx]
   [repo]
   [repos.postgres]))

(def dev? (= config/APP_ENV "dev"))

(def htmx-config
  (htmx/make-htmx-config
   {:repo :postgres}))

(def express-config
  (express/make-express-config
   {:htmx-config htmx-config
    :static-files-root (if dev?
                         "src/dev"
                         "src")}))

(def express-app
  (express/create-app express-config))

(def serverless-app (serverless express-app))

(when dev?
  (express/start-server express-app))

#js {:handler serverless-app}
