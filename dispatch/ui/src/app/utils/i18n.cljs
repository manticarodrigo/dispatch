(ns app.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [app.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:location/title "My location"
    :location/get "Get location"
    :location/watch "Watch location"
    :route-view {:common/distance "Distance"
                 :common/duration "Duration"
                 :panel-header/title "Route"
                 :list-empty/message "No route found. Make sure you enable location permissions."}}
   :es
   {:location/title "Mi ubicación"
    :location/get "Obtener ubicación"
    :location/watch "Observar ubicación"
    :route-view {:common/distance "Distancia"
                 :common/duration "Duración"
                 :panel-header/title "Ruta"
                 :list-empty/message "No se ha encontrado ninguna ruta. Asegurese de habilitar los permisos de ubicación."}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (or (listen [:locale/language]) :en))])
   args))

(def locales {:en-US {:language "en" :region "US"}
              :es-ES {:language "es" :region "ES"}})
