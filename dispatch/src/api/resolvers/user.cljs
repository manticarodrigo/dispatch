(ns api.resolvers.user
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.user :as user]))

(defn create-user
  [_ args context _]
  (user/create-user context (->clj args)))

(defn login
  [_ args context _]
  (user/login context (->clj args)))

(defn login-confirm
  [_ args context _]
  (user/login-confirm context (->clj args)))

(defn active-user
  [_ _ context _]
  (user/active-user context))

(defn fetch-scope
  [_ _ context _]
  (user/fetch-scope context))
