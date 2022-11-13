(ns api.lib.apollo
  (:require ["@apollo/server" :refer (ApolloServer)]
            ["@apollo/server/express4" :refer (expressMiddleware)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [promesa.core :as p]
            [api.lib.prisma :refer (open-prisma)]
            [api.util.prisma :refer (find-unique)]
            [api.util.anom :as anom]
            [api.resolvers.user :as user]
            [api.resolvers.seat :as seat]
            [api.resolvers.address :as address]))

(defn get-type-defs []
  (inline "schema.graphql"))

(def resolvers {:Mutation {:createUser user/create-user
                           :loginUser user/login-user
                           :createSeat seat/create-seat
                           :createAddress address/create-address}
                :Query {:user user/logged-in-user
                        :seats seat/find-seats
                        :addresses address/find-addresses}
                :User {:id #(.-id %)
                       :seats seat/find-seats
                       :addresses address/find-addresses}
                :Seat {:id #(.-id %)
                       :name #(.-name %)}
                :Address {:id #(.-id %)
                          :name #(.-name %)
                          :lat #(-> % ->clj :lat)
                          :lng #(-> % ->clj :lng)}})

(def options
  (->js {:context (fn [^js ctx]
                    (p/let [^js prisma (open-prisma)
                            session-id (some-> ctx .-req .-headers .-authorization)
                            ^js session (find-unique (. prisma -session)
                                                     {:where {:id session-id}
                                                      :include {:user true}})
                            ^js user (some-> session .-user)]
                      (->js {:prisma prisma :user user})))}))

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
