(ns ui.views.admin.core
  (:require [ui.lib.router :as router]
            [ui.views.admin.route.core :as route]
            [ui.views.admin.seat.core :as seat]
            [ui.views.admin.address.core :as address]
            [ui.views.admin.stop.core :as stop]))

(def route {:path "admin"
            :element [router/auth-route [router/outlet]]
            :children [{:path "routes" :element [route/list-view]}
                       {:path "routes/:id" :element [route/detail-view]}
                       {:path "routes/create" :element [route/create-view]}
                       {:path "seats" :element [seat/list-view]}
                       {:path "seats/:id" :element [seat/detail-view]}
                       {:path "seats/create" :element [seat/create-view]}
                       {:path "addresses" :element [address/list-view]}
                       {:path "addresses/:id" :element [address/detail-view]}
                       {:path "addresses/create" :element [address/create-view]}
                       {:path "stops/:id" :element [stop/detail-view]}]})
