(ns ui.components.loaders.scope
  (:require [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (navigate)]
            [ui.components.loaders.base :rename {loader base-loader}]))

(def FETCH_USER_SCOPE (gql (inline "queries/user/fetch-scope.graphql")))

(defn loader []
  (let [{:keys [data loading]} (use-query FETCH_USER_SCOPE {})
        {:keys [scope]} (:user data)
        url (case scope
              "organization" "/organization"
              "agent" "/agent"
              "/landing")]
    (if loading
      [base-loader]
      [navigate url])))
