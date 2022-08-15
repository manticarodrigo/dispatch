(ns app.views.route
  (:require
   [react]
   [clojure.string :as s]
   [app.subs :as subs]
   [app.hooks.route :refer (use-route
                            use-route-context
                            route-context-provider)]
   [app.utils.i18n :refer (tr)]))

(defn- list-empty []
  (let [{get-position :get} (use-route-context)]
    [:div {:class "p-2 sm:p-3"}
     [:p {:class "text-sm"} (tr [:route-view.list-empty/message])]
     [:button {:class "mt-2 rounded p-2 text-slate-700 hover:text-slate-800 bg-slate-200 hover:bg-slate-300"
               :on-click get-position} (tr [:route-view.list-empty/button])]]))

(defn- list-item-number [num]
  [:div {:class
         (s/join
          " "
          ["inline-block"
           "flex-none flex justify-center items-center"
           "rounded-full"
           "w-6 h-6"
           "text-slate-50"
           "bg-slate-800"])} num])

(defn- list-item-content [desc details]
  [:div {:class "pl-2"}
   [:div {:class "font-medium text-sm leading-4"} desc]
   [:div {:class "pt-1 text-xs leading-4"}
    details]])

(defn- detail [label desc]
  [:div [:span {:class "font-medium"} label] " - " desc])

(defn- panel-header []
  [:div {:class "border-b border-slate-300 pt-3 pb-2 px-2 bg-slate-50"}
   [:h1 {:class "font-semibold text-lg sm:text-xl md:text-2xl text-slate-900"} (tr [:route-view.panel-header/title])]])

(defn- panel-summary []
  (let [mins (subs/listen [:route/minutes])
        kms (subs/listen [:route/kilometers])]
    [:div {:class "p-2 sm:p-3 text-sm text-slate-50 bg-slate-700"}
     [:h2 {:class "font-semibold text-base text-slate-200"} (tr [:route-view.panel-summary/title])]
     [detail (tr [:route-view.generic/distance]) (str kms " km")]
     [detail (tr [:route-view.generic/duration]) (str mins " mins")]]))

(defn- panel-list []
  (let [route (subs/listen [:route/current])]
    [:<>
     (when (= (count route) 0)
       [:f> list-empty])
     (for [[idx
            {address :address
             distance :distance
             duration :duration}]
           (map-indexed vector route)]
       [:div {:key idx :class "border-b border-slate-200 flex p-2 sm:p-3"}
        [list-item-number (+ 1 idx)]
        [list-item-content address
         [:<>
          [detail (tr [:route-view.generic/distance]) (:text distance)]
          [detail (tr [:route-view.generic/duration]) (:text duration)]]]])]))

(defn- panel []
  [:div {:class "z-10 relative flex-none w-[300px] h-full bg-white drop-shadow-lg"}
   [panel-header]
   [panel-summary]
   [panel-list]])

(defn- gmap []
  (let [{!el :ref} (use-route-context)]
    [:div
     {:ref (fn [el] (reset! !el el))
      :class "w-full h-full"}]))

(defn route-view []
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "relative flex w-full h-screen"}
      [panel]
      [:f> gmap]]]))
