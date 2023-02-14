(ns ui.views.not-found
  (:require [ui.utils.i18n :refer (tr)]))

(defn view []
  [:div {:class "flex justify-center items-center w-full h-full"}
   [:p {:class "text-2xl"} (tr [:view/not-found])]])
