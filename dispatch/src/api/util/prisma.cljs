(ns api.util.prisma
  (:require ["@prisma/client" :refer (Prisma)]
            [cljs-bean.core :refer (->js)]))

(defn known-prisma-error? [e]
  (instance? (.. Prisma -PrismaClientKnownRequestError) e))

(defn filter-params [params]
  (into {} (filter (comp some? val) params)))

(defn create! [^js model params]
  (.create model (->js params)))

(defn update! [^js model params]
  (.update model (->js params)))

(defn find-unique [^js model params]
  (.findUnique model (->js params)))

(defn find-many [^js model params]
  (.findMany model (->js params)))
