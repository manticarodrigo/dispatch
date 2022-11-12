(ns ui.views.fleet
  (:require
   [react-feather :rename {User UserIcon
                           ChevronRight ChevronRightIcon}]
   [clojure.string :as s]
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

(defn view []
  (fn []
    [:ul {:class (class-names padding)}
     (for [{:keys [name]} items]
       ^{:key name} ;; add real keys!
       [:li
        [link {:to (seat-url name)
               :class (class-names "mb-2 block" button-class)}
         [item name "12:15pm" "active"]]])
     (for [{:keys [name]} inactive-items]
       ^{:key name} ;; add real keys!
       [:li
        [link {:to (seat-url name)
               :class (class-names "mb-2 block" button-class)}
         [item name "01/20/2022" "inactive"]]])]))

