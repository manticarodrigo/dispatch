(ns ui.views.fleet
  (:require
   ["@apollo/client" :refer (gql useQuery)]
   [react-feather :rename {User UserIcon
                           ChevronRight ChevronRightIcon
                           Plus PlusIcon}]
   [reagent.core :as r]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj)]
   [ui.lib.router :refer (link)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.input :refer (input)]
   [ui.components.inputs.generic.button :refer (button-class)]))

(defn item [name time status]
  [:div {:class (class-names "flex justify-between w-full")}
   [:div {:class "flex items-center"}
    [:div {:class "mr-2"} [:> UserIcon {:class "w-4 h-4"}]]
    [:div {:class "font-medium text-lg text-left"} name]]
   [:div {:class "flex items-center"}
    [:div {:class "flex flex-col items-end"}
     [:div {:class "flex items-center text-xs text-neutral-400"} "Last seen"]
     [:div {:class "flex items-center text-xs text-left text-neutral-200"}
      [:div {:class (class-names
                     "mr-1 rounded-full w-2 h-2"
                     (if (= status "active") "bg-green-500" "bg-amber-500"))}]
      time]]
    [:div {:class "ml-2"} [:> ChevronRightIcon]]]])

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
           {:to "/seat/create"
            :class button-class}
           [:span {:class "sr-only"} "Add seat"]
           [:> PlusIcon]]]

         [:ul
          (for [{:keys [id name]} filtered-seats]
            ^{:key id}
            [:li
             [link {:to (str "/seat/" id)
                    :class (class-names "mb-2 block" button-class)}
              [item name "12:15pm" "active"]]])
          (when (and (not loading) (empty? seats)) [:p {:class "text-center"} "No seats found."])
          (when loading [:p {:class "text-center"} "Loading seats..."])]]))))
