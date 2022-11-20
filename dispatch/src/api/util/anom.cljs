(ns api.util.anom
  (:require ["graphql" :refer (GraphQLError)]
            [api.util.prisma :refer (known-prisma-error?)]
            [cljs-bean.core :refer (->clj ->js)]))

(defn factory [category]
  (fn
    ([reason]
     {:category category :reason reason})
    ([reason meta]
     {:category category :reason reason :meta meta})))

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

(defn gql [anom]
  (GraphQLError.
   "An anomaly was detected."
   (->js {:extensions {:code "ANOMALY_DETECTED" :anom anom}})))

(defn parse-prisma-anom [^js e]
  (let [code (.-code e)
        meta (.-meta e)]
    (cond
      (= code "P2002") (conflict :unique-constraint (->clj meta))
      :else (fault :unknown))))

(defn parse-anom [^js e]
  (cond
    (known-prisma-error? e) (parse-prisma-anom e)
    :else (fault :unknown)))

(defn handle-resolver-error [e]
  (if (= "GraphQLError" (.-name e)) e
      (gql (parse-anom e))))
