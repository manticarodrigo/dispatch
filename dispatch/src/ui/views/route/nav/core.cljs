(ns ui.views.route.nav.core
  (:require
   [ui.utils.string :refer (class-names)]
   [ui.views.route.nav.container :refer (container)]
   [ui.views.route.nav.header :refer (header)]
   [ui.views.route.nav.controls :refer (controls)]
   [ui.views.route.nav.summary :refer (summary)]
   [ui.views.route.nav.overview :refer (overview)]))

(defn nav [class]
  [container (class-names class "flex flex-col")
   [header "flex-none"]
   [controls "flex-none"]
   [summary "flex-none"]
   [overview "grow"]])
