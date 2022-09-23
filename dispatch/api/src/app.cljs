(ns app
  (:require [dev.local-repo]
      ;;  [dynamo-repo]
            [express]
            [htmx]
            ["serverless-http$default" :as serverless]))

; parameters from the environment
(def lambda-base-url "https://api.dispatch.ambito.app")
(def allowed-origin-url "*")

(def htmx-config
  (htmx/make-htmx-config
   {:repo :local
    :post-comment-url (str lambda-base-url "/comments")}))

(def express-config
  (express/make-express-config
   {:htmx-config htmx-config
    :static-files-root "src"
    :allowed-origin-url allowed-origin-url}))

(def express-app
  (express/create-app express-config))

(def serverless-app (serverless express-app))

(dev.local-repo/seed)

#js {:handler serverless-app}

