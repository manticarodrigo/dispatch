(ns ui.views.admin.fleet.panel.core
  (:require
   [ui.utils.string :refer (class-names)]
   [ui.views.admin.fleet.panel.container :refer (container)]
   [ui.views.admin.fleet.panel.overview :refer (overview)]))

(defn panel [class]
  [container (class-names class "flex flex-col")
   [overview]])
