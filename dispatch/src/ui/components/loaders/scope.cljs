(ns ui.components.loaders.scope
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (navigate)]
            [ui.components.loaders.base :rename {loader base-loader}]))

(def FETCH_SCOPE (gql (inline "queries/user/scope.graphql")))

(defn loader []
  (let [{:keys [data loading]} (use-query FETCH_SCOPE {})
        {:keys [scope]} data
        url (case scope
              "organization" "/organization"
              "agent" "/agent"
              "/login")]
    (if loading
      [base-loader]
      [navigate url])))
