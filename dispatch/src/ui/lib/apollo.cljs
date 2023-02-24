(ns ui.lib.apollo
  (:require ["@apollo/client"
             :as apollo
             :refer (ApolloClient
                     InMemoryCache
                     ApolloProvider
                     createHttpLink
                     useQuery
                     useMutation)]
            ["@apollo/client/link/context" :refer (setContext)]
            ["@apollo/client/link/error" :refer (onError)]
            ["@graphql-tools/schema" :refer (makeExecutableSchema)]
            [apollo-link-scalars :refer (withScalars)]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [cljs-bean.core :refer (->clj ->js)]
            [common.utils.date :refer (date-scalar-map)]
            [common.utils.json :refer (json-scalar-map)]
            [ui.config :as config]
            [ui.utils.session :refer (get-session-request remove-session)]))

(def gql apollo/gql)

(defn parse-anoms [^js e]
  (if-let [anoms (mapv #(-> % :extensions :anom) (some-> e .-graphQLErrors ->clj))]
    anoms
    (throw e)))

(defonce http-link
  (createHttpLink
   (->js {:uri config/API_URL})))

(defonce auth-link
  (setContext #(->js (get-session-request))))

(defonce error-link
  (onError
   (fn [^js res]
     (when (= (some-> res .-networkError .-statusCode) 401)
       (remove-session))
     (condp (fn [reason anoms] (some #(= (:reason %) reason) anoms)) (parse-anoms res)
       "agent-not-found" (dispatch [:device/error "agent-not-found"])
       "device-token-invalid" (dispatch [:device/error "device-token-invalid"])
       "device-already-linked" (dispatch [:device/error "device-already-linked"])
       "device-not-linked" (dispatch [:device/error "device-not-linked"])
       nil))))

(def resolvers (->js {:Date date-scalar-map
                      :JSON json-scalar-map}))

(def schema (makeExecutableSchema (->js {:typeDefs (gql (inline "schema.graphql"))
                                         :resolvers resolvers})))

(defonce scalar-link
  (withScalars
   (->js {:schema schema
          :typesMap resolvers})))

(defonce client
  (ApolloClient.
   (->js {:link (apollo/from #js[scalar-link auth-link error-link http-link])
          :cache (InMemoryCache.)})))

(defn apollo-provider [& children]
  [:> ApolloProvider {:client client}
   (into [:<>] children)])

(defn use-query [query options]
  (let [q (useQuery query (->js (merge options {:fetchPolicy "network-only"})))]
    (->clj q)))

(defn use-mutation [query options]
  (let [[fn res] (useMutation query (->js options))]
    [#(fn (->js %)) (->clj res)]))
