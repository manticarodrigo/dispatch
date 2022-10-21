(ns util.anom
  (:require ["graphql" :refer (GraphQLError)]
            [cljs-bean.core :refer (->js)]))

(defn gql [anom]
  (GraphQLError. "An anomaly was detected." (->js {:extensions {:code "ANOMALY_DETECTED" :anomaly anom}})))

(defn factory [category]
  (fn
    ([reason]
     {:category category :reason reason})
    ([reason errors]
     {:category category :reason reason :errors errors})))

(def unavailable
  "make sure callee healthy"
  (factory :unavailable))

(def interrupted
  "stop interrupting"
  (factory :interrupted))

(def incorrect
  "fix caller bug"
  (factory :incorrect))

(def forbidden
  "fix caller creds"
  (factory :forbidden))

(def unsupported
  "fix caller verb"
  (factory :unsupported))

(def not-found
  "fix caller noun"
  (factory :not-found))

(def conflict
  "coordinate with callee"
  (factory :conflict))

(def fault
  "fix callee bug"
  (factory :fault))

(def busy
  "backoff and retry"
  (factory :busy))
