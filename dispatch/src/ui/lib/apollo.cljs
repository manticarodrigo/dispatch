(ns ui.lib.apollo
  (:require ["@apollo/client"
             :as apollo
             :refer (ApolloClient
                     InMemoryCache
                     ApolloProvider
                     createHttpLink
                     useQuery)]
            ["@apollo/client/link/context" :refer (setContext)]
            ["@apollo/client/link/error" :refer (onError)]
            [cljs-bean.core :refer (->clj ->js)]
            [ui.config :as config]
            [ui.utils.session :refer (get-session-request remove-session)]))

(defonce http-link
  (createHttpLink
   (->js {:uri config/API_URL})))

(defonce auth-link
  (setContext #(->js (get-session-request))))

(defonce error-link
  (onError
   (fn [^js res]
     (when (= (some-> res .-networkError .-statusCode) 401)
       (remove-session)))))

(defonce client
  (ApolloClient.
   (->js {:link (apollo/from #js[auth-link error-link http-link])
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
