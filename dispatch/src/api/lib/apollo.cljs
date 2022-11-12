(ns api.lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@apollo/server/express4" :refer (expressMiddleware)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]
            [api.lib.prisma :refer (open-prisma)]
            [api.util.anom :as anom]
            [api.resolvers.user :as user]
            [api.resolvers.seat :as seat]))

(defn get-type-defs []
  (inline "schema.graphql"))

(def resolvers {:Query {:findUser user/find-user}
                :Mutation {:createUser user/create-user
                           :loginUser user/login-user
                           :createSeat seat/create-seat}})

(def options
  (->js {:context (fn []
                    (p/let [prisma (open-prisma)]
                      (->js {:prisma prisma})))}))

(defn format-error [formatted-error _]
  (let [clj-error (->clj formatted-error)
        anom (-> clj-error :extensions :anom)]
    (if anom
      (->js clj-error)
      (->js (assoc-in clj-error [:extensions :anom] (anom/fault :unknown))))))

(defn create-server []
  (p/let [type-defs (get-type-defs)]
    (ApolloServer. (->js
                    {:typeDefs type-defs
                     :resolvers resolvers
                     :formatError format-error}))))

(defn start-server []
  (p/let [server (create-server)
          _ (.start server)]
    server))

(defn create-middleware [server]
  (expressMiddleware server options))
