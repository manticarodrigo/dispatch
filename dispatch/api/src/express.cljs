(ns express
  (:require ["express$default" :as express]
            ["http" :as http]
            [promesa.core :as p]
            [goog.object :as gobj]
            [htmx]
            [resolvers.comments]))

(defn extract-param
  [param req]
  (let [param-type (:type param)
        val (cond
              (= param-type :body) (gobj/getValueByKeys req "body" (:name param))
              (= param-type :query) (gobj/getValueByKeys req "query" (:name param))
              :else nil)]
    (when (and (nil? val) (:required param))
      (throw (js/Error. (str "missing required parameter " param))))
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
        param-kw-names (map (comp keyword :name) params)]
    (into {} (map vector param-kw-names values))))

(defn get-comments-handler
  [{:keys [htmx-config]}]
  (fn [req res]
    (p/let [{:keys [post-id]} (extract-params [{:type :query :name "post-id" :required true}] req)
            list-comments-response (htmx/get-comments htmx-config post-id)
            ;; list-comments-response (resolvers.comments/get-comments htmx-config post-id)
            ]
      (.send res list-comments-response))))

(defn post-comment-handler
  [{:keys [htmx-config]}]
  (fn [req res]
    (p/let [payload-config [{:type :body :name "author" :required true}
                            {:type :body :name "message" :required true}
                            {:type :body :name "post-id" :required true}]
            payload (extract-params payload-config req)]
      (p/let [add-comment-response (htmx/post-comment htmx-config payload)]
        (.send res add-comment-response)))))

(defn get-comments-form-handler
  [{:keys [htmx-config]}]
  (fn [req res]
    (let [{:keys [post-id]} (extract-params [{:type :query :name "post-id" :required true}] req)
          comments-form-html (htmx/get-comments-form-html htmx-config post-id)]
      (.send res comments-form-html))))

(defn make-express-config
  [user-config]
  (let [defaults {:allowed-origin-url "http://localhost:8080"}]
    (merge defaults user-config)))

(defn create-app
  [{:keys [allowed-origin-url static-files-root] :as config}]
  (let [app (express)]
    (.use app (.urlencoded express #js {:extended true}))
    (.use app (fn [_ res next]
                (doto res
                  (.set "Access-Control-Allow-Origin" allowed-origin-url)
                  (.set "Access-Control-Allow-Methods" "GET, POST")
                  (.set "Access-Control-Allow-Headers" "hx-trigger, hx-target, hx-request, hx-current-url"))
                (next)))

    (when static-files-root
      (.use app (.static express static-files-root)))

    (.get app "/comments" (get-comments-handler config))
    (.post app "/comments" (post-comment-handler config))
    (.get app "/comments-form" (get-comments-form-handler config))

    app))

(defn start-server
  [app & {:keys [port callback]
          :or {port 3000
               callback (fn [] (.log js/console "Listening on port 3000!"))}}]
  (let [server (.createServer http app)]
    (.listen server port callback)))

(defn stop-server
  [server]
  (.close server))
