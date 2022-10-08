(ns app
  (:require
   ["serverless-http$default" :as serverless]
   [config]
   [deps]
   [express]))

(def dev? (= config/APP_ENV "dev"))

(def express-app
  (express/create-app))

(def serverless-app (serverless express-app))

(when dev?
  (express/start-server express-app))

#js {:handler serverless-app}
