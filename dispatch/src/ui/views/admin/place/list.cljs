(ns ui.views.admin.place.list
  (:require [react]
            [react-feather :rename {MapPin PinIcon}]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.link-card :refer (link-card)]
            [ui.components.status-detail :refer (status-detail)]))

(def FETCH_PLACES (gql (inline "queries/place/fetch-all.graphql")))

(defn view []
  (let [[{:keys [text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query FETCH_PLACES {})
        places (some-> data :places)
        filtered-places (filter-text text :name places)]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:points
                   (mapv
                    (fn [{:keys [lat lng name]}]
                      {:title name
                       :position {:lat lat :lng lng}})
                    filtered-places)}])
       #())
     #js[places text])

    [:div {:class padding}
     [title {:title (tr [:view.place.list/title])
             :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params (if (empty? %)
                                                       (dissoc search-params :text)
                                                       (assoc search-params :text %)))}]
     [:ul
      (for [{:keys [id name description]} filtered-places]
        (let [active? false]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to id
                       :icon PinIcon
                       :title name
                       :subtitle description
                       :detail [status-detail
                                {:active? active?
                                 :text (if active? "Active" "Inactive")}]}]]))
      (if loading
        [:p {:class "text-center"} "Loading places..."]
        (when (empty? filtered-places)
          [:p {:class "text-center"} "No places found."]))]]))
