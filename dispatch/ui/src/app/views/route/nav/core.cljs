(ns app.views.route.nav.core
  (:require
   [app.views.route.nav.container :refer (container)]
   [app.views.route.nav.header :refer (header)]
   [app.views.route.nav.controls :refer (controls)]
   [app.views.route.nav.summary :refer (summary)]
   [app.views.route.nav.overview :refer (overview)]))

(defn nav []
  [:<>
   [container "flex flex-col text-white bg-black"
    [:<>
     [:f> header "flex-none"]
     [:f> controls "flex-none"]
     [:f> summary "flex-none"]
     [:f> overview "grow"]]]])
