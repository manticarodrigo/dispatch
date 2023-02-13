(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]))

(def ^:private dict
  {:en
   {:view {:register {:title "Register"}
           :login {:title "Login"}
           :task {:list {:title "Tasks"}
                  :create {:title "Create task"}}
           :seat {:list {:title "Seats"}
                  :create {:title "Create seat"}}
           :place {:list {:title "Places"}
                   :create {:title "Create place"}}}
    :field {:email "Email"
            :password "Password"
            :name "Name"
            :phone "Phone"
            :location "Location"
            :note "Note"
            :seat "Seat"
            :departure "Departure"
            :origin "Origin"
            :destination "Destination"
            :stops "Stops"
            :add-stop "Add stop"
            :submit "Submit"}
    :generic {:loading "Loading"
              :empty-search "No results found"}
    :map {:center "Center map"}
    :error {:unknown "An unknown error occurred."
            :unique-constraint "The account already exists."
            :invalid-password "The password is incorrect."
            :account-not-found "The account you're looking for does not exist."}}
   :es
   {:view {:register {:title "Registrar"}
           :login {:title "Ingresar"}
           :task {:list {:title "Tareas"}
                  :create {:title "Crear tarea"}}
           :seat {:list {:title "Asientos"}
                  :create {:title "Crear asiento"}}
           :place {:list {:title "Places"}
                   :create {:title "Crear lugar"}}}
    :field {:email "Correo electrónico"
            :password "Contraseña"
            :name "Nombre"
            :phone "Teléfono"
            :location "Ubicación"
            :note "Nota"
            :seat "Asiento"
            :departure "Salida"
            :origin "Origen"
            :destination "Destino"
            :stops "Paradas"
            :add-stop "Agregar parada"
            :submit "Enviar"}
    :generic {:loading "Cargando"
              :empty-search "No se han encontrado resultados"}
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
