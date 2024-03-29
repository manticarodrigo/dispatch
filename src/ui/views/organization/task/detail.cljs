(ns ui.views.organization.task.detail
  (:require ["react-feather" :rename {Edit EditIcon}]
            [clojure.string :as s]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-params link)]
            [ui.utils.i18n :refer (tr)]
            [ui.hooks.use-map :refer (use-map-items)]
            [ui.components.layout.map :refer (map-layout)]
            [ui.components.inputs.button :refer (button-class)]
            [ui.components.lists.stop :refer (stop-list)]))

(def FETCH_ORGANIZATION_TASK (gql (inline "queries/user/organization/fetch-task.graphql")))

(defn view []
  (let [{task-id :task} (use-params)
        query (use-query FETCH_ORGANIZATION_TASK {:variables {:taskId task-id}})
        {:keys [data loading]} query
        {:keys [task]} (some-> data :user :organization)
        {:keys [agent stops route]} task
        {:keys [name]} agent]

    (use-map-items
     loading
     {:tasks [task]
      :places (mapv :place stops)}
     [route stops])

    [map-layout {:title name
                 :actions [link {:to "update" :class button-class}
                           [:span {:class "flex items-center"}
                            [:> EditIcon {:class "mr-2 w-4 h-4"}]
                            (s/capitalize (tr [:verb/update]))]]}
     [stop-list {:task task :loading loading}]]))
