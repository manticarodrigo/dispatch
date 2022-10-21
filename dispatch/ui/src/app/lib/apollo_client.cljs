(ns app.lib.apollo-client
  (:require ["@apollo/client" :refer (ApolloClient InMemoryCache ApolloProvider)]
            [cljs-bean.core :refer (->js)]
            [app.config :as config]))

(defonce client (ApolloClient. (->js {:uri config/API_URL :cache (InMemoryCache.)})))

(defn apollo-provider [& children]
  [:> ApolloProvider {:client client}
   (into [:<>] children)])
