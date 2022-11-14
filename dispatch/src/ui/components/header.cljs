(ns ui.components.header
  (:require
   [react-feather
    :rename {ArrowLeft ArrowLeftIcon
             Settings SettingsIcon}]
   [ui.lib.router :refer (routes link)]
   [react-router-dom :refer (useSearchParams)]
   [cljs-bean.core :refer (->clj ->js)]
   [ui.utils.i18n :refer (tr)]
   [ui.utils.string :refer (class-names)]
   [ui.utils.css :refer (padding-x)]
   [ui.components.icons :as icons]
   [ui.components.inputs.generic.menu :refer (menu)]
   [ui.components.inputs.generic.modal :refer (modal)]
   [ui.components.forms.user :refer (user-form)]
   [ui.components.nav :refer (nav)]))

(defn menu-item [to label]
  [link {:to to :class "block w-full h-full text-sm"} label])

(defn title [text]
  [:h1 {:class
        (class-names
         "sr-only"
         "font-semibold text-white text-sm sm:text-base lg:text-xl")}
   text])

(defn header []
  (let [[search-params set-search-params] (useSearchParams)
        query (-> (.fromEntries js/Object search-params) ->clj)]
    [:header {:class (class-names
                      "flex-shrink-0"
                      "flex justify-between items-center"
                      padding-x
                      "w-full h-[60px]")}
     [routes
      ["/register" [icons/dispatch]]
      ["/login" [icons/dispatch]]
      ["/fleet" [icons/dispatch]]
      ["/address" [icons/dispatch]]
      ["/route" [icons/dispatch]]
      ["*" [link {:to "/fleet"} [:> ArrowLeftIcon]]]]

     [routes
      ["/register" [title (tr [:view.register/title])]]
      ["/login" [title (tr [:view.login/title])]]
      ["/fleet" [title (tr [:view.fleet/title])]]
      ["/address" [title (tr [:view.address/title])]]
      ["/route" [title (tr [:view.route/title])]]
      ["*" [title "Not found"]]]

     [nav]

     [menu {:label [:> SettingsIcon]
            :items [[{:label [menu-item "?modal=seat" "Add seat"]}
                     {:label [menu-item "/register" "Register"]}
                     {:label [menu-item "/login" "Login"]}]
                    {:label [menu-item "/logout" "Sign out..."]}]
            :class-map {:button! "h-full"
                        :item "min-w-[12rem]"}}]

     [modal {:show (= "seat" (:modal query))
             :title "Add seat"
             :on-close #(set-search-params (->js (dissoc query :modal)))}
      [user-form]]]))
