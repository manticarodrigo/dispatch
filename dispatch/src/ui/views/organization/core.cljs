(ns ui.views.organization.core
  (:require [ui.lib.router :as router]
            [ui.lib.platform :refer (platform)]
            [ui.views.organization.subscription.core :as subscription]
            [ui.views.organization.task.core :as task]
            [ui.views.organization.agent.core :as agent]
            [ui.views.organization.place.core :as place]
            [ui.views.organization.stop.core :as stop]))

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
                       (when
                        (= platform "web")
                         {:path "subscription/payment" :element [subscription/payment-view]})]})