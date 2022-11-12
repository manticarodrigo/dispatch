(ns tests.util.api
  (:require [goog.object :as gobj]
            [promesa.core :as p]
            [cljs-bean.core :refer (->clj ->js)]
            [api.lib.apollo :refer (create-server options)]))

(defonce !server (atom nil))

(defn init-server []
  (p/let [server (if @!server @!server (create-server))]
    (reset! !server server)
    server))

(defn obj->clj
  [^js obj]
  (if #_{:clj-kondo/ignore [:unresolved-symbol]}
   (goog.isObject obj)
    (-> (fn [result key]
          (let [v (goog.object/get obj key)]
            (if (= "function" (goog/typeOf v))
              result
              (assoc result (keyword key) (obj->clj v)))))
        (reduce {} (.getKeys ^js goog/object obj)))
    obj))


(defn send [body]
  (p/let [^js server (init-server)
          context (.context options)
          ^js res (.executeOperation server (->js body) (->js {:contextValue context}))
          {:keys [data errors]} (->clj (.. res -body -singleResult))]
    {:data (obj->clj data)
     :errors errors}))
