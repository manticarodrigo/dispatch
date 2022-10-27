(ns api.lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@apollo/server/express4" :refer (expressMiddleware)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]
            [api.schema :as schema]
            [api.lib.sequelize :refer (open-sequelize close-sequelize sync-sequelize)]
            [api.util.anom :as anom]
            [api.resolvers.user :refer (register login delete)]))


(defn get-type-defs []
  (inline "schema.graphql"))

(def resolvers {:Query {:hello (fn [] "world")}
                :Mutation {:register register
                           :login login
                           :delete delete}})

(defn init-models [sequelize]
  (p/let [user (schema/user sequelize)
          _ (sync-sequelize sequelize)]
    {:user user}))

(def options
  (->js {:context (fn []
                    (p/let [sequelize (open-sequelize)
                            models (init-models sequelize)]
                      (->js {:sequelize sequelize :models (->js models)})))}))

(def plugins
  (->js
   [{:requestDidStart
     (fn []
       (->js
        {:willSendResponse
         (fn [^js res]
           (close-sequelize (some-> res .-contextValue .-sequelize)))}))}]))

(defn format-error [formatted-error _]
  (let [clj-error (->clj formatted-error)
        anom (-> clj-error :extensions :anom)]
    (if anom
      (->js clj-error)
      (->js (assoc-in clj-error [:extensions :anom] (anom/fault :unknown))))))

(defn create-server []
  (p/let [type-defs (get-type-defs)
          server (ApolloServer. (->js
                                 {:typeDefs type-defs
                                  :resolvers resolvers
                                  :plugins plugins
                                  :formatError format-error}))
          _ (.start server)]
    server))

(defn create-middleware [server]
  (expressMiddleware server options))
