(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:view {:register {:title "Register"}
           :login {:title "Login"}
           :task {:list {:title "Tasks"}}
           :seat {:list {:title "Seats"}}
           :place {:list {:title "Places"}}}
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
           :task {:list {:title "Tareas"}}
           :seat {:list {:title "Asientos"}}
           :place {:list {:title "Places"}}}
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
   (partial tempura/tr opts [(keyword (condp = (listen [:language])
                                        "en" "en"
                                        "es" "es"
                                        "en"))])
   args))
