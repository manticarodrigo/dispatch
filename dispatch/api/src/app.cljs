(ns app
  (:require
   ["@apollo/server" :refer (ApolloServer)]
   ["@as-integrations/aws-lambda" :refer (startServerAndCreateLambdaHandler)]
   [cljs-bean.core :refer (->js)]
   [promesa.core :as p]
   [config]
   [deps]
   [models.user]
   [util.sequelize :refer (open-sequelize close-sequelize sync-sequelize)]
   [resolvers.auth :refer (login)]))

(def type-defs "#graphql
  type Query {
    hello: String
  }
  type Mutation {
    login(email: String, password: String): String
  }
")

(def resolvers {:Query {:hello (fn [] "world")}
                :Mutation {:login login}})

(defn init-models [sequelize]
  (p/let [user (models.user/init sequelize)
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
         (fn [res]
           (close-sequelize (some-> res .-contextValue .-sequelize)))}))}]))

#js {:handler (fn [event context]
                (p/let [handler (startServerAndCreateLambdaHandler
                                 (ApolloServer. (->js
                                                 {:typeDefs type-defs
                                                  :resolvers resolvers
                                                  :plugins plugins}))
                                 options)]
                  (handler event context)))}
