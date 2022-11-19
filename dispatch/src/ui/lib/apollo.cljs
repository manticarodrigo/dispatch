(ns ui.lib.apollo
  (:require ["@apollo/client"
             :as apollo
             :refer (ApolloClient
                     InMemoryCache
                     ApolloProvider
                     createHttpLink
                     useQuery)]
            ["@apollo/client/link/context" :refer (setContext)]
            [cljs-bean.core :refer (->clj ->js)]
            [ui.config :as config]
            [ui.utils.session :refer (get-session-request)]))

(defonce http-link
  (createHttpLink
   (->js {:uri config/API_URL})))

(defonce auth-link
  (setContext #(get-session-request)))

(defonce client
  (ApolloClient.
   (->js {:link (.concat auth-link http-link)
          :cache (InMemoryCache.)})))

(defn apollo-provider [& children]
  [:> ApolloProvider {:client client}
   (into [:<>] children)])

(defn parse-anoms [^js e]
  (let [anoms (mapv #(-> % :extensions :anom) (some-> e .-graphQLErrors ->clj))]
    anoms))

(def gql apollo/gql)

(defn use-query [query options]
  (let [q (useQuery query (->js options))]
    (->clj q)))
