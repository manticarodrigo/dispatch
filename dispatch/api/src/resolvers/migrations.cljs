(ns resolvers.migrations
  (:require ["node-pg-migrate$default" :as pgm]
            [promesa.core :as p]
            [config]
            [repo]))

(def db-url
  (str
   "postgres://"
   config/PGUSER
   ":"
   config/PGPASSWORD
   "@"
   config/PGHOST
   ":"
   config/PGPORT
   "/"
   config/PGDATABASE))

(def base-options {:databaseUrl db-url
                   :migrationsTable "migrations"
                   :dir "migrations"
                   :direction "up"
                   :count 1
                   :ignorePattern ""})

(defn make-options
  ([]
   (clj->js base-options))
  ([overrides]
   (clj->js (merge base-options overrides))))

(defn migrate-once [opts]
  (p/let [res (.default pgm opts)]
    (first res)))

(defn migrate
  ([]
   (migrate-once (make-options)))
  ([overrides]
   (migrate-once (make-options overrides))))

(defn up [_]
  (p/let [res (migrate)]
    (clj->js
     {:status 200
      :body (if res
              (str "Successfully migrated up: " (.-name res))
              "No migrations to run.")})))

(defn down [_]
  (p/let [res (migrate {:direction "down"})]
    (clj->js
     {:status 200
      :body (if res
              (str "Successfully migrated down: " (.-name res))
              "No migrations to run.")})))
