
(ns repos.postgres
  (:require
   [nbb.core :refer (slurp)]
   [promesa.core :as p]
   [cljs-bean.core :as bean]
   ["pg$default" :refer (Pool)]
   [repo]))

(defn- slurp-sql [path]
  (slurp (str "src/sql/" path ".sql")))

(def pool (new Pool))

(def ^:private client-singleton (atom nil))

(defn- setup-client []
  (or @client-singleton
      (p/let [client (.connect pool)
              get-sql (slurp-sql "exists-table")
              create-sql (slurp-sql "create-table")
              table-exists-res (.query client get-sql
                                       #js["comments"])
              table-exists? (-> table-exists-res .-rows first .-exists)
              _ (when-not table-exists?
                  (.query client create-sql))]
        (reset! client-singleton client)
        client)))

(defmethod repo/save-comment :postgres
  [config new-comment]
  (p/let [client (setup-client)
          sql (slurp-sql "insert-values")
          {post-id :post-id message :message author :author} new-comment]
    (.query client sql #js[post-id message (.toISOString (js/Date.)) author])
    new-comment))

(defn- get-db-comments
  [config post-id]
  (p/let [client (setup-client)
          sql (slurp-sql "select-rows")
          result (.query client sql #js[post-id])]
    (.-rows result)))

(def field-mapping
  {:id :post-id
   :message :message
   :time :time
   :author :author})

(defn- deserialize-comment
  [comment]
  (let [name-mapping-fn (fn [[k v]] [(k field-mapping) v])]
    (into {} (map name-mapping-fn comment))))

(defn- list-comments
  [config post-id]
  (p/let [results (get-db-comments config post-id)
          comments (map deserialize-comment (bean/->clj results))]
    (reverse (sort-by :time comments))))

(defmethod repo/get-comments :postgres
  [config post-id]
  (list-comments config post-id))