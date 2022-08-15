(ns app.utils.i18n
  (:require [taoensso.tempura :as tempura]))

(def ^:private dict
  {:en {:route-view
        {:generic/distance "Distance"
         :generic/duration "Duration"
         :panel-header/title "Route"
         :panel-summary/title "Summary"
         :list-empty/message "No route found. Make sure you enable location permissions."
         :list-empty/button "Get location"}}
   :es {:route-view
        {:generic/distance "Distancia"
         :generic/duration "Duración"
         :panel-header/title "Ruta"
         :panel-summary/title "Resumen"
         :list-empty/message "No se ha encontrado ninguna ruta. Asegurese de habilitar los permisos de ubicación."
         :list-empty/button "Obtener ubicacion"}}})

(def ^:private opts {:dict dict})

(def tr (partial tempura/tr opts [:en]))
