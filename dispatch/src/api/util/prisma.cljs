(ns api.util.prisma
  (:require ["@prisma/client" :refer (Prisma)]
            [cljs-bean.core :refer (->js)]))

(defn known-prisma-error? [e]
  (instance? (.. Prisma -PrismaClientKnownRequestError) e))

(defn select-params [p k]
  (into {} (filter
            (comp some? val)
            (select-keys p k))))

(defn create [^js model data]
  (.create model (->js {:data data})))

(defn push [^js model where column value]
  (.update model (->js {:where where
                        :data (assoc-in {} [column :push] value)})))

(defn find-unique-where [^js model where]
  (.findUnique model (->js {:where where})))

(defn delete-where [^js model where]
  (.delete model (->js {:where where})))
