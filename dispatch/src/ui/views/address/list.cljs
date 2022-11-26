(ns ui.views.address.list
  (:require
   ["@apollo/client" :refer (gql useQuery)]
   [react-feather :rename {MapPin PinIcon
                           ChevronRight ChevronRightIcon
                           Plus PlusIcon}]
   [reagent.core :as r]
   [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj)]
   [ui.lib.router :refer (link)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
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
      [:div {:class "ml-2"} [:> ChevronRightIcon]]]]))

(def FETCH_ADDRESSES (gql (inline "queries/address/fetch-all.graphql")))

(defn view []
  (let [!search (r/atom "")]
    (fn []
      (let [query (useQuery FETCH_ADDRESSES)
            {:keys [data loading]} (->clj query)
            addresses (some-> data :addresses)]
        [:div {:class (class-names padding)}
         [:div {:class "mb-4 flex justify-between"}
          [input {:aria-label "Search"
                  :value @!search
                  :placeholder "Search addresses"
                  :class "w-full mr-2"
                  :on-text #(reset! !search %)}]
          [link
           {:to "/addresses/create"
            :class button-class}
           [:span {:class "sr-only"} "Add address"]
           [:> PlusIcon]]]

         [:ul
          (for [{:keys [id] :as address} addresses]
            ^{:key id}
            [:li
             [link {:to (str "/addresses/" id)
                    :class (class-names "mb-2 block" button-class)}
              [item address]]])
          (when (and (not loading) (empty? addresses)) [:p {:class "text-center"} "No addresses found."])
          (when loading [:p {:class "text-center"} "Loading addresses..."])]]))))
