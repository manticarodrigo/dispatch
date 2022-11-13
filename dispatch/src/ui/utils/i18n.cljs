(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:units/kilometers "kilometers"
    :units/minutes "minutes"
    :units/km "km"
    :units/min "min"
    :location/title "My location"
    :location/center "Center route"
    :location/get "Get current location"
    :location/watch "Watch current location"
    :location/search "Set origin address"
    :location/search-empty "No results found"
    :view {:register {:title "Register"
                      :fields {:username "Username"
                               :email "Email"
                               :password "Password"
                               :submit "Submit"}}
           :login {:title "Login"
                   :fields {:email "Email"
                            :password "Password"
                            :submit "Submit"}}
           :fleet {:title "Fleet"
                   :distance "Total distance"
                   :duration "Estimated time"
                   :summary {:title "Summary"}
                   :overview {:title "Route"}}
           :schedule {:title "Schedule"}
           :address {:title "Address"}}
    :error {:unknown "An unknown error occurred."
            :unique-constraint "The account already exists."
            :invalid-password "The password is incorrect."
            :account-not-found "The account you're looking for does not exist."}}


   :es
   {:units/kilometers "kilometros"
    :units/minutes "minutos"
    :units/km "km"
    :units/min "min"
    :location/title "Mi ubicación"
    :location/center "Centrar la ruta"
    :location/get "Obtener ubicación actual"
    :location/watch "Observar ubicación actual"
    :location/search "Ingresar ubicación de origen"
    :location/search-empty "No se han encontrado resultados"
    :view {:register {:title "Registrar"
                      :fields {:username "Usuario"
                               :email "Correo electrónico"
                               :password "Contraseña"
                               :submit "Enviar"}}
           :login {:title "Ingresar"
                   :fields {:email "Correo electrónico"
                            :password "Contraseña"
                            :submit "Enviar"}}
           :fleet {:title "Flota"
                   :distance "Distancia total"
                   :duration "Tiempo estimado"
                   :summary {:title "Resumen"}
                   :overview {:title "Ruta"}}
           :schedule {:title "Programar"}
           :address {:title "Dirección"}}
    :error {:unknown "Un error desconocido ocurrió."
            :unique-constraint "La cuenta ya existe."
            :invalid-password "La contraseña es incorrecta."
            :account-not-found "La cuenta que busca no existe."}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (or (listen [:locale/language]) :en))])
   args))

(def locales {:en-US {:language "en" :region "US"}
              :es-ES {:language "es" :region "ES"}})
