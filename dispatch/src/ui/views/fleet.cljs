(ns ui.views.fleet
  (:require
   ["@apollo/client" :refer (gql useQuery)]
   [react-feather :rename {User UserIcon
                           ChevronRight ChevronRightIcon}]
   [clojure.string :as s]
   [shadow.resource :refer (inline)]
   [cljs-bean.core :refer (->clj)]
   [ui.lib.router :refer (link)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
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

(def items [{:name "Edwin Vega"}
            {:name "Billy Armstrong"}
            {:name "Felipe Carapaz"}
            {:name "Walter Van Aert"}])

(def inactive-items [{:name "Diego Wiggins"}
                     {:name "Alfredo Contador"}
                     {:name "Maria Van Vluten"}])

(defn seat-url [name]
  (str "/seat/" (->> (s/split name #"\s")
                     (s/join "-"))))

(def GET_SEATS (gql (inline "queries/seat/find.graphql")))

(defn view []
  (let [query (useQuery GET_SEATS)
        {:keys [data loading]} (->clj query)
        seats (some-> data :findSeats)]
    [:<>
     [:ul {:class (class-names padding)}
      (for [{:keys [id name]} seats]
        ^{:key id}
        [:li
         [link {:to (seat-url id)
                :class (class-names "mb-2 block" button-class)}
          [item name "12:15pm" "active"]]])
      (when (and (not loading) (empty? seats)) [:p {:class "text-center"} "No seats found."])
      (when loading [:p {:class "text-center"} "Loading seats..."])]]))
