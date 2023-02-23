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
            [api.resolvers.device :as device]
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
                {:createUser user/create
                 :createSession user/login
                 :createDevice device/create
                 :createAgent agent/create
                 :createPlace place/create
                 :createTask task/create
                 :createLocation location/create
                 :createArrival stop/create-arrival
                 :detachPaymentMethod stripe/detach-payment-method}
                :Query
                {:user user/active-user
                 :agents agent/find-all
                 :agent agent/find-unique
                 :places place/find-all
                 :place place/find-unique
                 :tasks task/find-all
                 :task task/find-unique
                 :stop stop/find-unique
                 :stripe #()}
                :User
                {:agents agent/find-all
                 :places place/find-all}
                :Place
                {:tasks task/find-by-place}
                :Stripe
                {:setupIntent stripe/create-setup-intent
                 :paymentMethods stripe/find-payment-methods}})

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
