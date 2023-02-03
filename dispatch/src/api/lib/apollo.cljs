(ns api.lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@apollo/server/express4" :refer (expressMiddleware)]
            ["@apollo/server/errors" :refer (unwrapResolverError)]
            [graphql :refer (GraphQLScalarType)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]
            [common.utils.date :refer (date-scalar-map)]
            [common.utils.json :refer (json-scalar-map)]
            [api.lib.prisma :refer (prisma)]
            [api.util.anom :as anom]
            [api.resolvers.user :as user]
            [api.resolvers.device :as device]
            [api.resolvers.seat :as seat]
            [api.resolvers.address :as address]
            [api.resolvers.route :as route]
            [api.resolvers.location :as location]
            [api.resolvers.stop :as stop]))

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
                {:createUser user/create-user
                 :loginUser user/login-user
                 :linkDevice device/link-device
                 :createSeat seat/create-seat
                 :createAddress address/create-address
                 :createRoute route/create-route
                 :createLocation location/create-location
                 :createStopArrival stop/create-stop-arrival}
                :Query
                {:user user/logged-in-user
                 :seats seat/fetch-seats
                 :seat seat/fetch-seat
                 :addresses address/fetch-addresses
                 :address address/fetch-address
                 :routes route/fetch-routes
                 :route route/fetch-route
                 :stop stop/fetch-stop}
                :User
                {:seats seat/fetch-seats
                 :addresses address/fetch-addresses}
                :Seat {}
                :Location {}
                :Address {:routes route/fetch-routes-by-address}
                :Stop {}
                :Route {}})

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
