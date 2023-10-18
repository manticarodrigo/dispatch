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

(defn upsert! [^js model params]
  (.upsert model (->js params)))

(defn delete! [^js model params]
  (.delete model (->js params)))

(defn delete-many! [^js model params]
  (.deleteMany model (->js params)))

(defn find-first [^js model params]
  (.findFirst model (->js params)))

(defn find-unique [^js model params]
  (.findUnique model (->js params)))

(defn find-unique-or-throw [^js model params]
  (.findUniqueOrThrow model (->js params)))

(defn find-many [^js model params]
  (.findMany model (->js params)))
