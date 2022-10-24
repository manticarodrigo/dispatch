(ns lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@as-integrations/aws-lambda" :refer (startServerAndCreateLambdaHandler)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]
            [config]
            [deps]
            [schema]
            [models.user]
            [lib.sequelize :refer (open-sequelize close-sequelize sync-sequelize)]
            [util.anom :as anom]
            [resolvers.auth :refer (register login)]))


(def type-defs "#graphql
  type Query {
    hello: String
  }
  type Mutation {
    register(firstName: String, lastName: String, email: String, password: String): String
    login(email: String!, password: String!): String!
  }
")

(def resolvers {:Query {:hello (fn [] "world")}
                :Mutation {:register register
                           :login login}})

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
         (fn [res]
           (close-sequelize (some-> res .-contextValue .-sequelize)))}))}]))

(defn format-error [formatted-error _]
  (let [clj-error (->clj formatted-error)
        anom (-> clj-error :extensions :anom)]
    (if anom
      (->js clj-error)
      (->js (assoc-in clj-error [:extensions :anom] (anom/fault :unknown))))))

(defn create-handler []
  (startServerAndCreateLambdaHandler
   (ApolloServer. (->js
                   {:typeDefs type-defs
                    :resolvers resolvers
                    :plugins plugins
                    :formatError format-error}))
   options))

(defn handler [event context]
  (p/let [handler (create-handler)]
    (handler event context)))
