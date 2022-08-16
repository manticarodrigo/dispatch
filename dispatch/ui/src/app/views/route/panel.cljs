(ns app.views.route.panel
  (:require
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [react-feather
    :refer (Menu GitPullRequest Clock)
    :rename {Menu MenuIcon GitPullRequest DistanceIcon Clock DurationIcon}]
   [app.subs :refer (listen)]
   [app.hooks.use-route :refer (use-route-context)]
   [app.utils.i18n :refer (tr locales)]
   [app.utils.string :refer (class-names)]
   [app.views.route.utils :refer (distance-str
                                  duration-str
                                  padding
                                  padding-x)]
   [app.views.route.overview :refer (overview)]))

(defn button [handler label class]
  [:button {:on-click handler
            :class (class-names
                    class
                    "rounded p-2 text-white/[0.8] hover:text-white bg-white/[0.2] hover:bg-white/[0.4]")}
   label])

(defn- panel-locale-option [locale label]
  [button (partial dispatch [:locale/set locale]) label])

(defn- panel-controls [order]
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

(def ^:private !open (r/atom false))

(defn panel [class]
  (js/console.log @!open)
  [:div
   {:class
    (class-names
     class
     "z-10 relative flex-none flex flex-col"
     "w-full lg:w-[450px] lg:h-full"
     "text-white")}
   [:div {:class (class-names padding-x "flex items-center h-[60px] bg-black")}
    [:button {:class "lg:hidden mr-2" :on-click #(swap! !open not)} [:> MenuIcon {:size 20}]]
    [:h1 {:class (class-names "font-semibold")}
     "Ambito " [:span {:class "font-light text-white/[0.8]"} "Dispatch"]]]
   [:div {:class (class-names
                  (if @!open "flex" "hidden")
                  "grow flex-none lg:flex flex-col absolute lg:relative top-[60px] lg:top-0 left-0 w-full h-[calc(100vh_-_60px)] bg-black")}
    [:f> panel-summary]
    [:f> panel-controls]
    [:f> overview]]])
