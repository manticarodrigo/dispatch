(ns ui.views.address.list
  (:require
   ["@apollo/client" :refer (gql)]
   [react]
   [react-feather :rename {MapPin PinIcon
                           ChevronRight ChevronRightIcon
                           Plus PlusIcon}]
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [ui.lib.apollo :refer (use-query)]
   [ui.lib.router :refer (link)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.utils.i18n :refer (tr)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.button :refer (button-class)]))

(defn item [{:keys [name description]}]
  (let [active? false]
    [:div {:class (class-names "flex justify-between w-full")}
     [:div {:class "flex items-center"}
      [:div {:class "mr-2"} [:> PinIcon {:class "w-4 h-4"}]]]
     [:div {:class "w-full"}
      [:div {:class "font-medium text-sm"} name]
      [:div {:class "font-light text-xs text-neutral-400"}
       description]]
     [:div {:class "flex items-center"}
      [:div {:class "flex flex-col items-end"}
       [:div {:class "flex items-center text-xs text-neutral-400"}
        [:div {:class (class-names
                       "mr-1 rounded-full w-2 h-2"
                       (if active? "bg-green-500" "bg-amber-500"))}]
        "Status"]
       [:div {:class "flex items-center text-xs text-left text-neutral-200"}
        (if active? "Active" "Inactive")]]
      [:div {:class "ml-2"} [:> ChevronRightIcon {:class "w-4 h-4"}]]]]))

(def FETCH_ADDRESSES (gql (inline "queries/address/fetch-all.graphql")))

(defn view []
  (let [!search (r/atom "")]
    (fn []
      (let [query (use-query FETCH_ADDRESSES {})
            {:keys [data loading]} query
            addresses (some-> data :addresses)
            filtered-addresses (if (empty? @!search)
                                 addresses
                                 (filter
                                  #(s/includes?
                                    (-> % :name s/lower-case)
                                    (s/lower-case @!search))
                                  addresses))
            markers (mapv
                     (fn [{:keys [lat lng name]}]
                       {:title name
                        :position {:lat lat
                                   :lng lng}})
                     filtered-addresses)]

        (react/useEffect
         (fn []
           (dispatch [:map/set-points markers])
           #(dispatch [:map/set-points nil]))
         #js[addresses @!search])

        [:div {:class (class-names padding)}
         [:div {:class "mb-4 flex justify-between items-center"}
          [:h1 {:class "text-lg"} (tr [:view.address.list/title])]
          [link {:to "/addresses/create" :class "underline text-sm"} [:> PlusIcon {:class "inline mr-1 w-3 h-3"}] "Create"]]
         [:div {:class "mb-4"}
          [input {:aria-label "Search"
                  :value @!search
                  :placeholder "Search addresses"
                  :class "w-full mr-2"
                  :on-text #(reset! !search %)}]]
         [:ul
          (for [{:keys [id] :as address} filtered-addresses]
            ^{:key id}
            [:li
             [link {:to (str "/addresses/" id)
                    :class (class-names "mb-2 block" button-class)}
              [item address]]])
          (when (and (not loading) (empty? filtered-addresses)) [:p {:class "text-center"} "No addresses found."])
          (when loading [:p {:class "text-center"} "Loading addresses..."])]]))))
