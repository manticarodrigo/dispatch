(ns api.lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@apollo/server/express4" :refer (expressMiddleware)]
            ["@apollo/server/errors" :refer (unwrapResolverError)]
            [graphql :refer (GraphQLScalarType)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [common.utils.date :refer (date-scalar-map)]
            [common.utils.json :refer (json-scalar-map)]
            [api.lib.prisma :refer (prisma)]
            [api.util.anom :as anom]
            [api.resolvers.user :as user]
            [api.resolvers.agent :as agent]
            [api.resolvers.place :as place]
            [api.resolvers.task :as task]
            [api.resolvers.location :as location]
            [api.resolvers.stop :as stop]
            [api.resolvers.stripe :as stripe]))

(def date-scalar
  (GraphQLScalarType.
   (->js (merge {:name "Date"
                 :description "Date custom scalar type"}
                date-scalar-map))))

(def json-scalar
  (GraphQLScalarType.
   (->js (merge {:name "JSON"
                 :description "JSON custom scalar type"}
                json-scalar-map))))

(def resolvers {:Date date-scalar
                :JSON json-scalar
                :Mutation
                {:loginPhone user/login-phone
                 :loginConfirm user/login-confirm
                 :createUser user/create-user
                 :createSession user/login
                 :createAgent agent/create-agent
                 :createPlace place/create-place
                 :createTask task/create-task
                 :createLocation location/create-location
                 :createArrival stop/create-arrival
                 :detachPaymentMethod stripe/detach-payment-method}
                :Query
                {:user #()
                 :stripe #()}
                :User
                {:scope user/fetch-scope
                 :organization #()
                 :agent #()}
                :Organization
                {:agents agent/fetch-organization-agents
                 :agent agent/fetch-organization-agent
                 :places place/fetch-organization-places
                 :place place/fetch-organization-place
                 :tasks task/fetch-organization-tasks
                 :task task/fetch-organization-task
                 :stop stop/fetch-organization-stop}
                :Agent
                {:places place/fetch-agent-places
                 :place place/fetch-agent-place
                 :tasks task/fetch-agent-tasks
                 :task task/fetch-agent-task
                 :stop stop/fetch-agent-stop}
                :Stripe
                {:setupIntent stripe/create-setup-intent
                 :paymentMethods stripe/fetch-payment-methods}})

(def options
  #js{:context
      (fn [^js ctx]
        (let [session (some-> ctx .-req .-headers ->clj :authorization)]
          #js{:prisma prisma :session session}))})

(defn format-error [formatted-error error]
  (let [clj-error (->clj formatted-error)
        anom (-> clj-error :extensions :anom)]
    (js/console.error "error" error)
    (if anom
      (->js clj-error)
      (anom/handle-error (unwrapResolverError error)))))

(defonce server
  (ApolloServer.
   (->js
    {:typeDefs (inline "schema.graphql")
     :resolvers resolvers
     :formatError format-error})))

(defn start-server []
  (.startInBackgroundHandlingStartupErrorsByLoggingAndFailingAllRequests server)
  server)

(defn create-middleware [server]
  (expressMiddleware server options))
