(ns app
  (:require
   [deps]
   ["serverless-http$default" :as serverless]
   [express]
   [htmx]
   [repo]
   [repos.postgres]))

; parameters from the environment
(def dev? false)
(def lambda-base-url "https://api.dispatch.ambito.app")
(def allowed-origin-url "*")

(def htmx-config
  (htmx/make-htmx-config
   {:repo (if dev?
            :atom
            :postgres)
    :post-comment-url (if dev?
                        "/comments"
                        (str lambda-base-url "/comments"))}))

(def express-config
  (express/make-express-config
   {:htmx-config htmx-config
    :static-files-root (if dev?
                         "src/dev"
                         "src")
    :allowed-origin-url allowed-origin-url}))

(def express-app
  (express/create-app express-config))

(def serverless-app (serverless express-app))

(if dev?
  (do
    (express/start-server express-app)
    (repo/save-comment {:repo :postgres} {:post-id "clojure-bandits" :message "Great post!" :time "12345" :author "Nick"})
    (repo/save-comment {:repo :postgres} {:post-id "clojure-bandits" :message "This post was ight" :time "999" :author "Jeremy"})
    (repo/save-comment {:repo :postgres} {:post-id "foo" :message "cool post!"}))
  #js {:handler serverless-app})
