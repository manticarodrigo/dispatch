(ns app.views.route
  (:require
   [react]
   [re-frame.core :as rf]
   [clojure.string :as s]
   [app.subs :as subs]
   [app.hooks.use-route :refer (use-route
                                use-route-context
                                route-context-provider)]
   [app.utils.i18n :refer (tr locales)]))

(defn class-names [& classes]
  (s/join
   " "
   classes))

(def ^:private distance-str (tr [:route-view.common/distance]))
(def ^:private duration-str (tr [:route-view.common/duration]))

(defn- container [class children]
  [:div {:class
         (class-names class
                      "z-10 relative flex-none"
                      "w-[300px] h-full "
                      "bg-white drop-shadow-lg overflow-y-auto")}
   children])

(defn- locale-option [locale label]
  [:button {:class "m-2 rounded p-2 text-slate-700 hover:text-slate-800 bg-slate-200 hover:bg-slate-300"
            :on-click #(rf/dispatch [:locale/set locale])} label])

(defn- locale-switch []
  [:div
   [locale-option (:en-US locales) "EN"]
   [locale-option (:es-ES locales) "ES"]])

(defn- controls [class]
  (let [origin (subs/listen [:origin])
        location (subs/listen [:location])
        {get-position :get watch-position :watch} (use-route-context)]
    [container
     class
     [:<>
      [locale-switch]
      (when (nil? origin)
        [:button {:class "m-2 rounded p-2 text-slate-700 hover:text-slate-800 bg-slate-200 hover:bg-slate-300"
                  :on-click get-position} (tr [:location/get])])
      (when (and (some? origin) (nil? location))
        [:button {:class "m-2 rounded p-2 text-slate-700 hover:text-slate-800 bg-slate-200 hover:bg-slate-300"
                  :on-click watch-position} (tr [:location/watch])])]]))

(defn- list-empty []
  [:div {:class "p-2 sm:p-3"}
   [:p {:class "text-sm"} (tr [:route-view.list-empty/message])]])

(defn- list-item-number [num]
  [:div {:class
         (class-names "inline-block"
                      "flex-none flex justify-center items-center"
                      "rounded-full"
                      "w-6 h-6"
                      "text-slate-50"
                      "bg-slate-800")} num])

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
  (let [title (tr [:route-view.panel-summary/title])
        kms (subs/listen [:route/kilometers])
        mins (subs/listen [:route/minutes])]
    [:div {:class "p-2 sm:p-3 text-sm text-slate-50 bg-slate-700"}
     [:h2 {:class "font-semibold text-base text-slate-200"} title]
     [detail distance-str (str kms " km")]
     [detail duration-str (str mins " mins")]]))

(defn- panel-list []
  (let [route (subs/listen [:route])]
    [:<>
     (when (= (count route) 0)
       [:f> list-empty])
     (doall
      (for [[idx
             {address :address
              distance :distance
              duration :duration}]
            (map-indexed vector route)]
        [:div {:key idx :class "border-b border-slate-200 flex p-2 sm:p-3"}
         [list-item-number (+ 1 idx)]
         [list-item-content address
          [:<>
           [detail distance-str (:text distance)]
           [detail duration-str (:text duration)]]]]))]))

(defn- panel [class]
  [container
   class
   [:<>
    [panel-header]
    [panel-summary]
    [panel-list]]])

(defn- gmap [class]
  (let [{!el :ref} (use-route-context)]
    [:div
     {:ref (fn [el] (reset! !el el))
      :class (class-names class "w-full h-full")}]))

(defn route-view []
  (let [props (use-route)]
    [:> route-context-provider {:value props}
     [:div {:class "relative flex w-full h-screen"}
      [:f> controls "order-1"]
      [:f> panel "order-3"]
      [:f> gmap "order-2"]]]))
