(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]
            [ui.utils.date :as d]))

(def ^:private dict
  {:en
   {:noun {:status "status"
           :never "never"}
    :verb {:create "create"}
    :view {:register {:title "Register"}
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
            :date "Select a date"
            :language "Select a language"
            :search "Type to search"
            :status "Select a status"
            :submit "Submit"}
    :status {:all "All"
             :incomplete "Incomplete"
             :complete "Complete"
             :active "Active"
             :inactive "Inactive"
             :starts "Starts"
             :started "Started"
             :last-seen (fn [[date]]
                          (str "Last seen "
                               (if date
                                 (d/formatRelative date (js/Date.))
                                 "never")))}
    :calendar {:previous-month "Previous month"
               :next-month "Next month"}
    :misc {:sign-out "Sign out"
           :loading "Loading"
           :empty-search "No results found"}
    :map {:center "Center map"}
    :error {:unknown "An unknown error occurred."
            :unique-constraint "The account already exists."
            :invalid-password "The password is incorrect."
            :account-not-found "The account you're looking for does not exist."}}
   :es
   {:noun {:status "estatus"
           :never "nunca"}
    :verb {:create "crear"}
    :view {:register {:title "Registrar"}
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
            :date "Selecciona una fecha"
            :language "Selecciona un idioma"
            :search "Escribe para buscar"
            :status "Selecciona un estado"
            :submit "Enviar"}
    :status {:all "Todos"
             :incomplete "Incompleto"
             :complete "Completo"
             :active "Activo"
             :inactive "Inactivo"
             :starts "Empieza"
             :started "Empezó"
             :last-seen (fn [[date]]
                          (str "Visto por última vez "
                               (if date
                                 (d/formatRelative date (js/Date.))
                                 "nunca")))}
    :calendar {:previous-month "Mes anterior"
               :next-month "Mes siguiente"}
    :misc {:sign-out "Cerrar sesión"
           :loading "Cargando"
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
