(ns app.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [app.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:location/title "My location"
    :location/search "Set origin address"
    :location/get "Get current location"
    :location/watch "Watch current location"
    :route-view {:common/distance "Distance"
                 :common/duration "Duration"
                 :panel-header/title "Route"}}
   :es
   {:location/title "Mi ubicación"
    :location/search "Ingresar ubicación de origen"
    :location/get "Obtener ubicación actual"
    :location/watch "Observar ubicación actual"
    :route-view {:common/distance "Distancia"
                 :common/duration "Duración"
                 :panel-header/title "Ruta"}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (or (listen [:locale/language]) :en))])
   args))

(def locales {:en-US {:language "en" :region "US"}
              :es-ES {:language "es" :region "ES"}})
