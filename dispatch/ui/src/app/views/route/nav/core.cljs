(ns app.views.route.nav.core
  (:require
   [app.utils.string :refer (class-names)]
   [app.views.route.nav.container :refer (container)]
   [app.views.route.nav.header :refer (header)]
   [app.views.route.nav.controls :refer (controls)]
   [app.views.route.nav.summary :refer (summary)]
   [app.views.route.nav.overview :refer (overview)]))

(defn nav [class]
  [:<>
   [container (class-names class "flex flex-col")
    [:<>
     [header "flex-none"]
     [controls "flex-none"]
     [summary "flex-none"]
     [overview "grow"]]]])
