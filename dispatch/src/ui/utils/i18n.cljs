(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]))

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
    :view {:register
           {:title "Register"
            :fields {:firstName "First name"
                     :lastName "Last name"
                     :email "Email"
                     :password "Password"
                     :submit "Submit"}}
           :login
           {:title "Login"
            :fields {:email "Email"
                     :password "Password"
                     :submit "Submit"}}
           :route
           {:distance "Total distance"
            :duration "Estimated time"
            :summary {:title "Summary"}
            :overview {:title "Route"}}}
    :error {:unknown "An unknown error occurred."
            :invalid-password "The supplied password is incorrect."
            :account-not-found "The account you're looking for does not exist."}}


   :es
   {:units/kilometers "kilometros"
    :units/minutes "minutos"
    :location/title "Mi ubicación"
    :location/center "Centrar la ruta"
    :location/get "Obtener ubicación actual"
    :location/watch "Observar ubicación actual"
    :location/search "Ingresar ubicación de origen"
    :location/search-empty "No se han encontrado resultados"
    :view {:register
           {:title "Registrar"
            :fields {:firstName "Nombre"
                     :lastName "Apellido"
                     :email "Correo electrónico"
                     :password "Contraseña"
                     :submit "Enviar"}}
           :login
           {:title "Ingresar"
            :fields {:email "Correo electrónico"
                     :password "Contraseña"
                     :submit "Enviar"}}
           :route
           {:distance "Distancia total"
            :duration "Tiempo estimado"
            :summary {:title "Resumen"}
            :overview {:title "Ruta"}}}
    :error {:unknown "Un error desconocido ocurrió."
            :invalid-password "La contraseña proporcionada es incorrecta."
            :account-not-found "La cuenta que busca no existe."}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (or (listen [:locale/language]) :en))])
   args))

(def locales {:en-US {:language "en" :region "US"}
              :es-ES {:language "es" :region "ES"}})
