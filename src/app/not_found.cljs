(ns app.not-found
  (:require ["react-feather" :rename {AlertTriangle AlertIcon
                                      ArrowLeft ArrowLeftIcon}]
            [reagent.core :as r]
            [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]))

(r/set-default-compiler! (r/create-compiler {:function-components true}))

(defn view []
  [:div {:class "flex flex-col justify-center items-center w-full h-full"}
   [:> AlertIcon {:class "w-8 h-8 text-neutral-500"}]
   [:p {:class "my-2 text-xl"} (tr [:view/not-found])]
   [link {:to "/" :class "flex items-center text-sm underline"}
    [:> ArrowLeftIcon {:class "mr-2 w-4 h-4"}] (tr [:misc/back-to-home])]])

(def ^:export NotFound (r/reactify-component view))
