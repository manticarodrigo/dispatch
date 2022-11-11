(ns ui.lib.apollo
  (:require ["@apollo/client" :refer (ApolloClient InMemoryCache ApolloProvider)] 
            [cljs-bean.core :refer (->clj ->js)]
            [ui.config :as config]))

(defonce client (ApolloClient. (->js {:uri config/API_URL :cache (InMemoryCache.)})))

(defn apollo-provider [& children]
  [:> ApolloProvider {:client client}
   (into [:<>] children)])

(defn parse-anoms [^js e]
  (let [anoms (mapv #(-> % :extensions :anom) (some-> e .-graphQLErrors ->clj))]
    anoms))
