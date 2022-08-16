(ns app.views.route.panel
  (:require
   [react-feather
    :refer (GitPullRequest Clock)
    :rename {GitPullRequest DistanceIcon Clock DurationIcon}]
   [re-frame.core :refer (dispatch)]
   [app.subs :refer (listen)]
   [app.hooks.use-route :refer (use-route-context)]
   [app.utils.i18n :refer (tr locales)]
   [app.utils.string :refer (class-names)]))

(def ^:private distance-str #(tr [:route-view.common/distance]))
(def ^:private duration-str #(tr [:route-view.common/duration]))

(def ^:private padding-x "px-2 sm:px-4 md:px-6 lg:px-8")
(def ^:private padding "p-2 sm:p-4 md:p-6 lg:p-8")

(defn button [handler label class]
  [:button {:on-click handler
            :class (class-names
                    class
                    "rounded p-2 text-white/[0.8] hover:text-white bg-white/[0.2] hover:bg-white/[0.4]")}
   label])

(defn- panel-container [class children]
  [:div {:class
         (class-names class
                      "z-10 relative flex-none"
                      "w-[300px] lg:w-[400px] h-full"
                      "text-white bg-black")}
   children])

(defn- panel-locale-option [locale label]
  [button (partial dispatch [:locale/set locale]) label])

(defn panel-controls [order]
  (let [origin (listen [:origin])
        location (listen [:location])
        {get-position :get watch-position :watch} (use-route-context)]
    [:div {:class (class-names padding "grid grid-cols-2 gap-4")}
     order
     [:<>
      [panel-locale-option (:en-US locales) "EN"]
      [panel-locale-option (:es-ES locales) "ES"]
      (when (nil? origin)
        [button get-position (tr [:location/get]) "col-span-2"])
      (when (and (some? origin) (nil? location))
        [button watch-position (tr [:location/watch]) "col-span-2"])]]))

(defn- panel-summary-item [label value icon]
  [:div {:class "flex flex-col"}
   [:span {:class "flex text-sm text-neutral-300 leading-4"} [:> icon {:size 15 :class "mr-1"}]  label]
   [:span {:class "flex text-lg leading-5"} value]])

(defn- panel-summary []
  (let [kms (listen [:route/kilometers])
        mins (listen [:route/minutes])]
    [:div {:class (class-names padding-x "grid grid-cols-2 gap-4")}
     [:h2 {:class "col-span-2 flex font-medium text-l"} (tr [:route-view.panel-header/title])]
     [panel-summary-item (distance-str) (str kms " km") DistanceIcon]
     [panel-summary-item (duration-str) (str mins " mins") DurationIcon]]))

(defn- panel-header []
  [:<>
   [:h1 {:class (class-names padding "font-semibold")}
    "Ambito " [:span {:class "font-light text-white/[0.8]"} "Dispatch"]]
   [:f> panel-summary]
   [:f> panel-controls]])

(defn- panel-list-item-number [num]
  [:div {:class
         (class-names "shrink-0 inline-block"
                      "flex-none flex justify-center items-center"
                      "rounded-full"
                      "w-8 h-8"
                      "font-bold text-white/[0.8] bg-white/[0.2]")} num])

(defn- panel-list-item-details [label value]
  [:div {:class "flex justify-center items-center mb-1 text-white/[0.6]"}
   [:span {:class "sr-only"} label " "]
   value])

(defn- panel-list-item [idx address distance duration]
  [:li {:class (class-names padding "flex py-2 hover:bg-white/[0.2]")}
   [panel-list-item-number (+ 1 idx)]
   [:div {:class "px-2 md:px-4 lg:px-6 grow font-medium text-sm leading-4"} address]
   [:div {:class "shrink-0 flex flex-col text-sm leading-4"}
    [panel-list-item-details (distance-str) (:text distance)]
    [panel-list-item-details (duration-str) (:text duration)]]])

(defn- panel-list-empty []
  [:p {:class (class-names padding "text-sm")} (tr [:route-view.list-empty/message])])

(defn- panel-list []
  (let [route (listen [:route])]
    [:<>
     (when (= (count route) 0)
       [:f> panel-list-empty])
     (doall
      (for [[idx
             {address :address
              distance :distance
              duration :duration}]
            (map-indexed vector route)]
        [:ol {:key idx}
         [panel-list-item idx address distance duration]]))]))

(defn panel [class]
  [panel-container
   class
   [:<>
    [panel-header]
    [panel-list]]])