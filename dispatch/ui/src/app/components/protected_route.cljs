(ns app.components.protected-route
  (:require ["react-router-dom" :refer (Navigate)]
            [app.utils.cookie :refer (get-session)]))

(defn protected-route [children]
  (if (get-session)
    children
    [:> Navigate {:to "/login" :replace true}]))
