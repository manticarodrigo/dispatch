(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:view {:register {:title "Register"}
           :login {:title "Login"}
           :route {:list {:title "Routes"}}
           :seat {:list {:title "Seats"}}
           :address {:list {:title "Addresses"}}}
    :fields {:email "Email"
             :password "Password"
             :submit "Submit"}
    :generic {:empty-search "No results found"}
    :map {:center "Center map"}
    :error {:unknown "An unknown error occurred."
            :unique-constraint "The account already exists."
            :invalid-password "The password is incorrect."
            :account-not-found "The account you're looking for does not exist."}}
   :es
   {:view {:register {:title "Registrar"}
           :login {:title "Ingresar"}
           :route {:list {:title "Rutas"}}
           :seat {:list {:title "Asientos"}}
           :address {:list {:title "Direcciones"}}}
    :fields {:email "Correo electrónico"
             :password "Contraseña"
             :submit "Enviar"}
    :generic {:empty-search "No se han encontrado resultados"}
    :map {:center "Centrar mapa"}
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
