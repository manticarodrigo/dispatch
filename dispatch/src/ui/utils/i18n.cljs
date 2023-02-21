(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]
            [ui.utils.date :as d]))

(def ^:private dict
  {:en
   {:noun {:status "status"
           :never "never"}
    :verb {:create "create"}
    :view {:register {:title "Register"
                      :login {:title "Already have an account?"
                              :link "Login here."}}
           :payment {:title "Payments"
                     :subtitle "Please enter your payment information for future use."}
           :login {:title "Login"
                   :register {:title "Need an account?"
                              :link "Register here."}}
           :task {:list {:title "Tasks"}
                  :create {:title "Create task"}}
           :agent {:list {:title "Agents"}
                   :create {:title "Create agent"}}
           :place {:list {:title "Places"}
                   :create {:title "Create place"}}
           :not-found "Page not found"}
    :field {:email "Email"
            :password "Password"
            :organization "Organization"
            :name "Name"
            :phone "Phone"
            :note "Note"
            :agent "Agent"
            :departure "Departure"
            :origin "Origin"
            :destination "Destination"
            :stops "Stops"
            :add-stop "Add stop"
            :date "Select a date"
            :language "Select a language"
            :search "Type to search"
            :status "Select a status"
            :location-search "Search location"
            :location-get "Get location"
            :description "Description"
            :latitude "Latitude"
            :longitude "Longitude"
            :submit "Submit"}
    :status {:all "All"
             :incomplete "Incomplete"
             :complete "Complete"
             :active "Active"
             :inactive "Inactive"
             :last-seen (fn [[date]]
                          (str "Seen "
                               (if date
                                 (d/formatRelative date (js/Date.))
                                 "never")))
             :start-at (fn [[date]]
                         (if date
                           (let [started? (d/isBefore date (js/Date.))]
                             (str (if started? "Started" "Starts")
                                  " "
                                  (d/formatDistanceToNowStrict date)))
                           "Loading..."))}
    :calendar {:previous-month "Previous month"
               :next-month "Next month"}
    :misc {:sign-out "Sign out"
           :loading "Loading"
           :empty-search "No results found"}
    :map {:center "Center map"}
    :location {:title "Tracking you"
               :message "Cancel to prevent battery drain."
               :permission "This app needs your location, but does not have permission.\n\nOpen settings now?"
               :unsupported "Location not supported on this platform."}
    :device {:unsupported {:title "Unsupported platform"
                           :message "Looks like you are trying to access an agent view from a web browser. Please use the mobile app to access this view."
                           :download "Download from Google Play Store"}
             :linked {:title "Another device already linked"
                      :message "Looks like this agent has a device linked to it already. If you would like to link your device to this agent, please reach out to an admin and ask them to unlink the other device first."}
             :unlinked {:title "No device linked"
                        :message "Looks like this agent has no device linked to it yet. Please press the button below to link your device and continue."}
             :link "Link Device"}
    :error {:unknown "An unknown error occurred."
            :unique-constraint "The account already exists."
            :invalid-password "The password is incorrect."
            :account-not-found "The account you're looking for does not exist."}}
   :es
   {:noun {:status "estado"
           :never "nunca"}
    :verb {:create "crear"}
    :view {:register {:title "Registrar"
                      :login {:title "¿Ya tienes una cuenta?"
                              :link "Ingresar aquí."}}
           :payment {:title "Pagos"
                     :subtitle "Por favor ingresa tu información de pago para futuros usos."}
           :login {:title "Ingresar"
                   :register {:title "¿Necesitas una cuenta?"
                              :link "Registrar aquí."}}
           :task {:list {:title "Tareas"}
                  :create {:title "Crear tarea"}}
           :agent {:list {:title "Agentes"}
                   :create {:title "Crear agente"}}
           :place {:list {:title "Lugares"}
                   :create {:title "Crear lugar"}}
           :not-found "Página no encontrada"}
    :field {:email "Correo electrónico"
            :password "Contraseña"
            :organization "Organización"
            :name "Nombre"
            :phone "Teléfono"
            :note "Nota"
            :agent "Agente"
            :departure "Salida"
            :origin "Origen"
            :destination "Destino"
            :stops "Paradas"
            :add-stop "Agregar parada"
            :date "Selecciona una fecha"
            :language "Selecciona un idioma"
            :search "Escribe para buscar"
            :status "Selecciona un estado"
            :location-search "Busca ubicación"
            :location-get "Obtén ubicación"
            :description "Descripción"
            :latitude "Latitud"
            :longitude "Longitud"
            :submit "Enviar"}
    :status {:all "Todos"
             :incomplete "Incompleto"
             :complete "Completo"
             :active "Activo"
             :inactive "Inactivo"
             :last-seen (fn [[date]]
                          (str "Visto "
                               (if date
                                 (d/formatRelative date (js/Date.))
                                 "nunca")))
             :start-at (fn [[date]]
                         (if date
                           (let [started? (d/isBefore date (js/Date.))]
                             (str (if started? "Empezó" "Empieza")
                                  " "
                                  (d/formatDistanceToNowStrict date)))
                           "Cargando..."))}
    :calendar {:previous-month "Mes anterior"
               :next-month "Mes siguiente"}
    :misc {:sign-out "Cerrar sesión"
           :loading "Cargando"
           :empty-search "No se han encontrado resultados"}
    :map {:center "Centrar mapa"}
    :location {:title "Rastreando"
               :message "Cancelar para evitar el consumo de batería."
               :permission "Esta aplicación necesita tu ubicación, pero no tiene permiso.\n\n¿Abrir configuraciones ahora?"
               :unsupported "Ubicación no soportada en esta plataforma."}
    :device {:unsupported {:title "Plataforma no soportada"
                           :message "Parece que estás tratando de acceder a una vista de agente desde un navegador web. Por favor usa la aplicación móvil para acceder a esta vista."
                           :download "Descargar desde Google Play Store"}
             :linked {:title "Otro dispositivo ya está vinculado"
                      :message "Parece que este agente ya tiene un dispositivo vinculado a él. Si quieres vincular tu dispositivo a este agente, por favor contacta a un administrador y pídele que desvincule el otro dispositivo primero."}
             :unlinked {:title "No hay ningún dispositivo vinculado"
                        :message "Parece que este agente no tiene ningún dispositivo vinculado a él todavía. Por favor presiona el botón de abajo para vincular tu dispositivo y continuar."}
             :link "Vincular Dispositivo"}
    :error {:unknown "Un error desconocido ocurrió."
            :unique-constraint "La cuenta ya existe."
            :invalid-password "La contraseña es incorrecta."
            :account-not-found "La cuenta que busca no existe."}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (listen [:language]))])
   args))
