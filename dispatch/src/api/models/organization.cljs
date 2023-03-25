(ns api.models.organization
  (:require [promesa.core :as p]
            [api.models.user :as user]))

(defn fetch-organization-metrics [^js context]
  (p/let [^js user (user/active-user context {:include
                                              {:organization
                                               {:include {}}}})]

    #js{}))
