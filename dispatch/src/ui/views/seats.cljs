(ns ui.views.seats
  (:require
   [react-feather :rename {Plus PlusIcon
                           Radio RadioIcon
                           Settings SettingsIcon}]
   [reagent.core :as r]
   [clojure.string :as s]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding)]
   [ui.components.inputs.generic.button :refer (button base-button-class)]
   [ui.components.inputs.generic.modal :refer (modal)]
   [ui.components.forms.user :refer (user-form)]))

(defn status-color [status]
  (condp = status
    "active" "bg-green-500"
    "inactive" "bg-yellow-500"
    "away" "bg-neutral-500"))

(def items [{:name "Alfredo Contador"
             :firstName "Alfredo"
             :lastName "Contador"
             :status "active"}
            {:name "Billy Armstrong"
             :firstName "Billy"
             :lastName "Armstrong"
             :status "inactive"}
            {:name "Diego Wiggins"
             :status "away"}
            {:name "Edwin Vega"
             :status "active"}
            {:name "Felipe Carapaz"
             :status "active"}
            {:name "Maria Van Vluten"
             :status "inactive"}])

(defn view []
  (let [!agent-to-show (r/atom nil)]
    (fn []
      [:div {:class (class-names padding)}
       [modal {:show (some? @!agent-to-show)
               :title "Manage agent"
               :on-close #(reset! !agent-to-show nil)}
        [user-form {:initial-state @!agent-to-show}]]

       [button {:label [:div {:class (class-names "flex justify-between items-center w-full")}
                        "Add new seat"
                        [:span {:class "ml-2"}
                         [:> PlusIcon {:class "w-5 h-5"}]]]
                :class "mb-4 w-full"
                :on-click #(reset! !agent-to-show {})}]
       [:ul
        (doall
         (for [item items]
           ^{:key (:name item)}
           [:li {:class (class-names "my-2 flex justify-between" base-button-class)}
            [:div {:class "font-medium text-xl text-left"}
             (-> item :name)
             [:div {:class "flex items-center font-light text-xs text-left text-neutral-400"}
              [:div {:class (class-names "mr-2 rounded-full w-2.5 h-2.5" (status-color (-> item :status)))}]
              (s/capitalize (-> item :status))]]
            [:div {:class "flex flex-col items-end" :on-click #(reset! !agent-to-show item)}
             [:button {:aria-label "Seat settings"}
              [:> SettingsIcon]]
             [:span {:class ""}
              [:span {:class "flex items-center text-sm leading-6"}
               [:span {:class "text-neutral-300"} [:> RadioIcon {:size 15 :class "mr-1"}]]
               "Today, 3:30pm"]]]]))]])))
