(ns api.resolvers.user
  (:require [cljs-bean.core :refer (->clj)]
            [api.models.user :as user]))

(defn register-user
  [_ args context _]
  (user/register-user context (->clj args)))

(defn login-user
  [_ args context _]
  (user/login-user context (->clj args)))

(defn login-confirm
  [_ args context _]
  (user/login-confirm context (->clj args)))

(defn active-user
  [_ _ context _]
  (user/active-user context))

(defn fetch-scope
  [_ _ context _]
  (user/fetch-scope context))
