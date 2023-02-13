(ns ui.views.admin.seat.list
  (:require [react]
            [react-feather :rename {User UserIcon}]
            [date-fns :as d]
            [re-frame.core :refer (dispatch)]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (use-search-params)]
            [ui.utils.string :refer (filter-text)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.title :refer (title)]
            [ui.components.filters :refer (filters)]
            [ui.components.status-detail :refer (status-detail)]
            [ui.components.link-card :refer (link-card)]))

(def FETCH_SEATS (gql (inline "queries/seat/fetch-all.graphql")))

(defn view []
  (let [[{:keys [text] :as search-params} set-search-params] (use-search-params)
        {:keys [data loading]} (use-query FETCH_SEATS {})
        seats (some-> data :seats)
        filtered-seats (filter-text text :name seats)]

    (react/useEffect
     (fn []
       (dispatch [:map
                  {:locations
                   (mapv
                    (fn [{:keys [name location]}]
                      {:title name
                       :position (:position location)})
                    (filter #(:location %) filtered-seats))}])
       #())
     #js[seats text])

    [:div {:class padding}
     [title {:title (tr [:view.seat.list/title]) :create-link "create"}]
     [filters {:search text
               :on-search-change #(set-search-params (if (empty? %)
                                                       (dissoc search-params :text)
                                                       (assoc search-params :text %)))}]
     [:ul
      (for [{:keys [id name location]} filtered-seats]
        (let [{:keys [createdAt]} location
              date (when createdAt (-> (js/parseInt createdAt) js/Date.))
              active? (when date (-> date (d/isAfter (d/subHours (js/Date.) 26))))]
          ^{:key id}
          [:li {:class "mb-2"}
           [link-card {:to id
                       :icon UserIcon
                       :title name
                       :subtitle (str "Last seen " (if date (-> date (d/formatRelative (js/Date.))) "never"))
                       :detail [status-detail
                                {:active? active?
                                 :text (if active? "Active" "Inactive")}]}]]))
      (if loading
        [:p {:class "text-center"} "Loading seats..."]
        (when (empty? filtered-seats)
          [:p {:class "text-center"} "No seats found."]))]]))
