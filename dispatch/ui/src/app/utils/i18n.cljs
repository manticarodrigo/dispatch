(ns app.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [app.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:units/kilometers "kilometers"
    :units/minutes "minutes"
    :location/title "My location"
    :location/center "Center route"
    :location/get "Get current location"
    :location/watch "Watch current location"
    :location/search "Set origin address"
    :location/search-empty "No results found"

    :views {:route
            {:distance "Total distance"
             :duration "Estimated time"
             :summary {:title "Summary"}
             :overview {:title "Route"}}}}
   :es
   {:units/kilometers "kilometros"
    :units/minutes "minutos"
    :location/title "Mi ubicación"
    :location/center "Centrar la ruta"
    :location/get "Obtener ubicación actual"
    :location/watch "Observar ubicación actual"
    :location/search "Ingresar ubicación de origen"
    :location/search-empty "No se han encontrado resultados"
    :views {:route
            {:distance "Distancia total"
             :duration "Tiempo estimado"
             :summary {:title "Resumen"}
             :overview {:title "Ruta"}}}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (or (listen [:locale/language]) :en))])
   args))

(def locales {:en-US {:language "en" :region "US"}
              :es-ES {:language "es" :region "ES"}})
