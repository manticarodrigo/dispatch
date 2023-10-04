(ns tests.util.api
  (:require ["@apollo/client" :refer (gql)]
            [goog.object :as gobj]
            [promesa.core :as p]
            [cljs-bean.core :refer (->clj ->js)]
            [api.lib.apollo :refer (server options)]
            [ui.utils.session :refer (get-session-request)]))

(defn obj->clj
  [^js obj]
  (if #_{:clj-kondo/ignore [:unresolved-symbol]}
   (goog.isObject obj)
    (-> (fn [result key]
          (let [v (goog.object/get obj key)]
            (cond (= "function" (goog/typeOf v)) result
                  (.isArray js/Array v) (assoc result (keyword key) (mapv #(obj->clj %) v))
                  :else (assoc result (keyword key) (obj->clj v)))))
        (reduce {} (.getKeys ^js goog/object obj)))
    obj))

(defn get-body-request [body]
  {:body
   {:operationName (some-> body :query gql ->clj :definitions first :name :value)}})

(defn send [body]
  (p/let [context (.context options (->js {:req (merge
                                                 (get-session-request)
                                                 (get-body-request body))}))
          ^js res (.executeOperation server (->js body) (->js {:contextValue context}))]
    (obj->clj (.. res -body -singleResult))))
