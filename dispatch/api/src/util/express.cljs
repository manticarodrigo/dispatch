(ns util.express
  (:require [goog.object :as gobj]
            [cljs-bean.core :refer (->js)]
            [util.sequelize :refer (parse-error)]))

(defn extract-param
  [param req]
  (let [param-type (:type param)
        val (cond
              (= param-type :body) (gobj/getValueByKeys req "body" (:name param))
              (= param-type :query) (gobj/getValueByKeys req "query" (:name param))
              :else nil)]
    (when (and (empty? val) (:required param))
      (throw (js/Error. (str "missing required field " "\"" (:name param) "\""))))
    val))

(defn extract-params
  "Extract parameters from an expressjs request.

  Can extract params from body or query params.

  Can specify which fields are required.

  Example-usage:
    (extract-params
      [{:type :body :name \"message\" :required true}
       {:type :query :name \"post-id\"}]
      req)"
  [params req]
  (let [values (map #(extract-param % req) params)
        param-kw-names (map (comp keyword :name) params)
        all-params (into {} (map vector param-kw-names values))]
    (into {} (filter (comp not-empty val) all-params))))

(defn send [res status body]
  (-> res (.status status) (.send (->js body))))

(defn handle-error-factory [res]
  (fn [e]
    (let [[status body] (parse-error e)]
      (send res status body))))
