(ns ui.views.admin.place.list
  (:require [react]
            [react-feather :rename {MapPin PinIcon
                                    Plus PlusIcon}]
            [reagent.core :as r]
            [re-frame.core :refer (dispatch)]
            [clojure.string :as s]
            [shadow.resource :refer (inline)]
            [ui.lib.apollo :refer (gql use-query)]
            [ui.lib.router :refer (link)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.css :refer (padding)]
            [ui.utils.i18n :refer (tr)]
            [ui.components.inputs.input :refer (input)]
            [ui.components.link-card :refer (link-card)]))

(def FETCH_PLACES (gql (inline "queries/place/fetch-all.graphql")))

(defn view []
  (let [!search (r/atom "")]
    (fn []
      (let [query (use-query FETCH_PLACES {})
            {:keys [data loading]} query
            places (some-> data :places)
            filtered-places (if (empty? @!search)
                              places
                              (filter
                               #(s/includes?
                                 (-> % :name s/lower-case)
                                 (s/lower-case @!search))
                               places))
            markers (mapv
                     (fn [{:keys [lat lng name]}]
                       {:title name
                        :position {:lat lat
                                   :lng lng}})
                     filtered-places)]

        (react/useEffect
         (fn []
           (dispatch [:map {:points markers}])
           #())
         #js[places @!search])

        [:div {:class (class-names padding)}
         [:div {:class "mb-4 flex justify-between items-center"}
          [:h1 {:class "text-lg"} (tr [:view.place.list/title])]
          [link {:to "/admin/places/create" :class "underline text-sm"} [:> PlusIcon {:class "inline mr-1 w-3 h-3"}] "Create"]]
         [:div {:class "mb-4"}
          [input {:aria-label "Search"
                  :value @!search
                  :placeholder "Search places"
                  :class "w-full mr-2"
                  :on-text #(reset! !search %)}]]
         [:ul
          (for [{:keys [id name description]} filtered-places]
            (let [active? false]
              ^{:key id}
              [:li {:class "mb-2"}
               [link-card {:to (str "/admin/places/" id)
                           :icon PinIcon
                           :title name
                           :subtitle description
                           :detail [:<>
                                    [:div {:class "flex items-center text-xs text-neutral-400"}
                                     [:div {:class (class-names
                                                    "mr-1 rounded-full w-2 h-2"
                                                    (if active? "bg-green-500" "bg-amber-500"))}]
                                     "Status"]
                                    [:div {:class "flex items-center text-xs text-left text-neutral-200"}
                                     (if active? "Active" "Inactive")]]}]]))
          (when (and (not loading) (empty? filtered-places)) [:p {:class "text-center"} "No places found."])
          (when loading [:p {:class "text-center"} "Loading places..."])]]))))
