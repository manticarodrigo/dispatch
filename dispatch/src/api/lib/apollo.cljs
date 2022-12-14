(ns api.lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@apollo/server/express4" :refer (expressMiddleware)]
            [graphql :refer (GraphQLScalarType)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]
            [common.utils.date :refer (date-scalar-map)]
            [api.lib.prisma :refer (open-prisma)]
            [api.util.prisma :refer (find-unique)]
            [api.util.anom :as anom]
            [api.resolvers.user :as user]
            [api.resolvers.seat :as seat]
            [api.resolvers.address :as address]
            [api.resolvers.route :as route]
            [api.resolvers.location :as location]
            [api.resolvers.stop :as stop]))

(defn get-type-defs []
  (inline "schema.graphql"))

(def date-scalar
  (GraphQLScalarType.
   (->js (merge {:name "Date"
                 :description "Date custom scalar type"}
                date-scalar-map))))

(def resolvers {:Date date-scalar
                :Mutation
                {:createUser user/create-user
                 :loginUser user/login-user
                 :createSeat seat/create-seat
                 :createAddress address/create-address
                 :createRoute route/create-route
                 :createLocation location/create-location
                 :createStopArrival stop/create-arrived-at}
                :Query
                {:user user/logged-in-user
                 :seats seat/fetch-seats
                 :seat seat/fetch-seat
                 :addresses address/fetch-addresses
                 :routes route/fetch-routes
                 :route route/fetch-route}
                :User
                {:id #(-> ^js % .-id)
                 :seats seat/fetch-seats
                 :addresses address/fetch-addresses}
                :Seat
                {:id #(-> ^js % .-id)
                 :name #(-> ^js % .-name)
                 :location #(-> ^js % .-location)}
                :Location
                {:id #(-> ^js % .-id)
                 :lat #(-> ^js % .-lat)
                 :lng #(-> ^js % .-lng)
                 :createdAt #(-> ^js % .-createdAt)}
                :Address
                {:id #(-> ^js % .-id)
                 :name #(-> ^js % .-name)
                 :description #(-> ^js % .-description)
                 :lat #(-> ^js % .-lat)
                 :lng #(-> ^js % .-lng)}
                :Stop
                {:id #(-> ^js % .-id)
                 :address #(-> ^js % .-address)
                 :arrivedAt #(-> ^js % .-arrivedAt)}
                :Route
                {:id #(-> ^js % .-id)
                 :seat #(-> ^js % .-seat)
                 :startAt #(-> ^js % .-startAt)
                 :stops #(-> ^js % .-stops)}})

(def options
  (->js {:context (fn [^js ctx]
                    (p/let [^js prisma (open-prisma)
                            session-id (some-> ctx .-req .-headers ->clj :authorization)
                            ^js session (when session-id
                                          (find-unique (. prisma -session)
                                                       {:where {:id session-id}
                                                        :include {:user true}}))
                            ^js user (some-> session .-user)
                            public-operation? (some
                                               #(= % (-> ctx .-req .-body .-operationName))
                                               ["CreateUser" "LoginUser"])]
                      (when (and (not public-operation?)
                                 (not user))
                        (anom/gql (anom/forbidden :invalid-session)))
                      (->js {:prisma prisma :user user})))}))

(defn format-error [formatted-error e]
  (let [clj-error (->clj formatted-error)
        anom (-> clj-error :extensions :anom)]
    (js/console.log "error" e)
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
