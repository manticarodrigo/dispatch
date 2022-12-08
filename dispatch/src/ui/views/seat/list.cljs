(ns ui.views.seat.list
  (:require
   ["@apollo/client" :refer (gql useQuery)]
   [react-feather :rename {User UserIcon
                           ChevronRight ChevronRightIcon
                           Plus PlusIcon}]
   [date-fns :as d]
   [reagent.core :as r]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj)]
   [ui.lib.router :refer (link)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.button :refer (button-class)]))

(defn item [{:keys [name location]}]
  (let [{:keys [createdAt]} location
        date (when createdAt (-> (js/parseInt createdAt) js/Date.))
        active? (when date (-> date (d/isAfter (d/subHours (js/Date.) 26))))]
    [:div {:class (class-names "flex justify-between w-full")}
     [:div {:class "flex items-center"}
      [:div {:class "mr-2"} [:> UserIcon {:class "w-4 h-4"}]]]
     [:div {:class "w-full"}
      [:div {:class "font-medium text-sm"} name]
      [:div {:class "font-light text-xs text-neutral-400"}
       "Last seen " (if date (-> date (d/formatRelative (js/Date.))) "never")]]
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

(def FETCH_SEATS (gql (inline "queries/seat/fetch-all.graphql")))

(defn view []
  (let [!search (r/atom "")]
    (fn []
      (let [query (useQuery FETCH_SEATS)
            {:keys [data loading]} (->clj query)
            seats (some-> data :seats)
            filtered-seats (if (empty? @!search)
                             seats
                             (filter
                              #(s/includes?
                                (-> % :name s/lower-case)
                                (s/lower-case @!search))
                              seats))]
        [:div {:class (class-names padding)}
         [:div {:class "mb-4 flex justify-between"}
          [input {:aria-label "Search"
                  :value @!search
                  :placeholder "Search seats"
                  :class "w-full mr-2"
                  :on-text #(reset! !search %)}]
          [link
           {:to "/seats/create"
            :class button-class}
           [:span {:class "sr-only"} "Add seat"]
           [:> PlusIcon]]]

         [:ul
          (for [{:keys [id] :as seat} filtered-seats]
            ^{:key id}
            [:li
             [link {:to (str "/seats/" id)
                    :class (class-names "mb-2 block" button-class)}
              [item seat]]])
          (when (and (not loading) (empty? filtered-seats)) [:p {:class "text-center"} "No seats found."])
          (when loading [:p {:class "text-center"} "Loading seats..."])]]))))
