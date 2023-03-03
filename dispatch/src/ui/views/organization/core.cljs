(ns ui.views.organization.core
  (:require [ui.lib.router :as router]
            [ui.lib.platform :refer (platform)]
            [ui.views.organization.subscription.core :as subscription]
            [ui.views.organization.task.core :as task]
            [ui.views.organization.agent.core :as agent]
            [ui.views.organization.place.core :as place]
            [ui.views.organization.stop.core :as stop]
            [ui.views.organization.shipment.core :as shipment]
            [ui.views.organization.vehicle.core :as vehicle]
            [ui.views.organization.plan.core :as plan]))

(def route {:path "organization"
            :element [router/auth-route [router/outlet]]
            :children [{:index true :element [router/navigate "tasks"]}
                       {:path "tasks" :element [task/list-view]}
                       {:path "tasks/:task" :element [task/detail-view]}
                       {:path "tasks/create" :element [task/create-view]}
                       {:path "agents" :element [agent/list-view]}
                       {:path "agents/:agent" :element [agent/detail-view]}
                       {:path "agents/create" :element [agent/create-view]}
                       {:path "places" :element [place/list-view]}
                       {:path "places/:place" :element [place/detail-view]}
                       {:path "places/create" :element [place/create-view]}
                       {:path "stops/:stop" :element [stop/detail-view]}
                       {:path "vehicles" :element [vehicle/list-view]}
                       {:path "vehicles/create" :element [vehicle/create-view]}
                       {:path "shipments" :element [shipment/list-view]}
                       {:path "shipments/create" :element [shipment/create-view]}
                       {:path "plans" :element [plan/list-view]}
                       {:path "plans/create" :element [plan/create-view]}
                       (when
                        (= platform "web")
                         {:path "subscription/payment" :element [subscription/payment-view]})]})
