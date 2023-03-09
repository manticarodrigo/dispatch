(ns ui.utils.i18n
  (:require [taoensso.tempura :as tempura]
            [ui.subs :refer (listen)]
            [ui.utils.date :as d]))

(def ^:private dict
  {:en
   {:noun {:status "status"
           :never "never"
           :shipments "shipments"
           :vehicles "vehicles"}
    :verb {:create "create"
           :show "show"
           :optimize "optimize"}
    :view {:not-found "Page not found"
           :register {:title "Register"
                      :login-link {:title "Already have an account?"
                                   :link "Login here."}}
           :login {:title "Login"
                   :register-link {:title "Need an account?"
                                   :link "Register here."}}
           :login-confirm {:title "Confirm login"
                           :return-link {:title "Didn't receive a code?"
                                         :link "Return to login."}}
           :task {:list {:title "Tasks"}
                  :create {:title "Create task"}}
           :agent {:list {:title "Agents"}
                   :create {:title "Create agent"}}
           :place {:list {:title "Places"}
                   :create {:title "Create place"}}
           :plan {:list {:title "Plans"}
                  :create {:title "Create plan"}
                  :detail {:title (fn [[startAt endAt]]
                                    (str
                                     "Plan"
                                     (when (and startAt endAt)
                                       (apply str " ("
                                              (d/format startAt "dd/MM/yyyy hh:mmaaa")
                                              " - "
                                              (d/format endAt "dd/MM/yyyy hh:mmaaa")
                                              ")"))))}}
           :shipment {:list {:title "Shipments"}
                      :create {:title "Create shipment"}}
           :vehicle {:list {:title "Vehicles"}
                     :create {:title "Create vehicle"}}
           :subscription {:title "Manage subscription"
                          :payment {:title "Payments"
                                    :subtitle "Manage your payment methods."
                                    :succeeded "Your payment method has been saved."
                                    :processing "Your payment method is being processed."
                                    :failed "Unable to save your payment method."
                                    :add-payment-method "Add payment method"
                                    :delete-payment-method "Delete payment method"}}}
    :field {:email "Email"
            :phone "Phone"
            :code "Code"
            :password "Password"
            :organization "Organization"
            :name "Name"
            :note "Note"
            :agent "Agent"
            :departure "Departure"
            :origin "Origin"
            :destination "Destination"
            :stops "Stops"
            :add-stop "Add stop"
            :date "Select a date"
            :language "Select a language"
            :search "Search..."
            :status "Select a status"
            :location-search "Search location"
            :location-get "Get location"
            :description "Description"
            :latitude "Latitude"
            :longitude "Longitude"
            :submit "Submit"}

    :table {:plan {:agent "Agent"
                   :vehicle "Vehicle"
                   :task "Task"
                   :start "Start"
                   :end "End"
                   :windows "Windows"
                   :arrival "Arrival"
                   :distance "Distance"
                   :volume "Volume"
                   :weight "Weight"
                   :visits "Visits"
                   :flexible-visits "Flexible visits"
                   :place "Place"
                   :order "Order"
                   :go-to-task "Go to task"
                   :view-skipped-shipments "View skipped shipments"}}

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
    :device {:unsupported
             {:title "Unsupported platform"
              :message "Looks like you are trying to access an agent view from a web browser. Please use the mobile app to access this view."
              :download "Download from Google Play Store"}}
    :error {:unknown "An unknown error occurred."
            :unique-constraint "The account already exists."
            :invalid-password "The password is incorrect."
            :account-not-found "The account you're looking for does not exist."
            :verification-not-found "The verification code you entered is invalid."}}
   :es
   {:noun {:status "estado"
           :never "nunca"
           :shipments "envíos"
           :vehicles "vehículos"}
    :verb {:create "crear"
           :show "mostrar"
           :optimize "optimizar"}

    :view {:register {:title "Registrar"
                      :login-link {:title "¿Ya tienes una cuenta?"
                                   :link "Ingresar aquí."}}
           :login {:title "Ingresar"
                   :register-link {:title "¿Necesitas una cuenta?"
                                   :link "Registrar aquí."}}
           :login-confirm {:title "Confirmar ingreso"
                           :return-link {:title "No recibiste el código?"
                                         :link "Regresar a ingresar."}}
           :task {:list {:title "Tareas"}
                  :create {:title "Crear tarea"}}
           :agent {:list {:title "Agentes"}
                   :create {:title "Crear agente"}}
           :place {:list {:title "Lugares"}
                   :create {:title "Crear lugar"}}
           :plan {:list {:title "Planes"}
                  :create {:title "Crear plan"}
                  :detail {:title (fn [[startAt endAt]]
                                    (str
                                     "Plan"
                                     (when (and startAt endAt)
                                       (apply str
                                              " ("
                                              (d/format startAt "dd/MM/yyyy hh:mmaaa")
                                              " - "
                                              (d/format endAt "dd/MM/yyyy hh:mmaaa")
                                              ")"))))}}
           :shipment {:list {:title "Envíos"}
                      :create {:title "Crear envío"}}
           :vehicle {:list {:title "Vehículos"}
                     :create {:title "Crear vehículo"}}
           :not-found "Página no encontrada"
           :subscription {:title "Administrar suscripción"
                          :payment {:title "Pagos"
                                    :subtitle "Administra tus métodos de pago."
                                    :succeeded "Tu método de pago ha sido guardado."
                                    :processing "Tu método de pago está siendo procesado."
                                    :failed "No se pudo guardar tu método de pago."
                                    :add-payment-method "Agregar método de pago"
                                    :delete-payment-method "Eliminar método de pago"}}}

    :field {:email "Correo electrónico"
            :phone "Teléfono"
            :code "Código"
            :password "Contraseña"
            :organization "Organización"
            :name "Nombre"
            :note "Nota"
            :agent "Agente"
            :departure "Salida"
            :origin "Origen"
            :destination "Destino"
            :stops "Paradas"
            :add-stop "Agregar parada"
            :date "Selecciona una fecha"
            :language "Selecciona un idioma"
            :search "Buscar..."
            :status "Selecciona un estado"
            :location-search "Busca ubicación"
            :location-get "Obtén ubicación"
            :description "Descripción"
            :latitude "Latitud"
            :longitude "Longitud"
            :submit "Enviar"}

    :table {:plan {:agent "Agente"
                   :vehicle "Vehículo"
                   :task "Tarea"
                   :windows "Ventanas"
                   :start "Inicio"
                   :end "Fin"
                   :arrival "Llegada"
                   :distance "Distancia"
                   :volume "Volumen"
                   :weight "Peso"
                   :visits "Visitas"
                   :flexible-visits "Visitas flexibles"
                   :place "Lugar"
                   :order "Orden"
                   :go-to-task "Ir a tarea"
                   :view-skipped-shipments "Ver envíos omitidos"}}

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
    :device {:unsupported
             {:title "Plataforma no soportada"
              :message "Parece que estás tratando de acceder a una vista de agente desde un navegador web. Por favor usa la aplicación móvil para acceder a esta vista."
              :download "Descargar desde Google Play Store"}}
    :error {:unknown "Un error desconocido ocurrió."
            :unique-constraint "La cuenta ya existe."
            :invalid-password "La contraseña es incorrecta."
            :account-not-found "La cuenta que busca no existe."
            :verification-not-found "El código de verificación que ingresaste es inválido."}}})

(def ^:private opts {:dict dict})

(defn tr [& args]
  (apply
   (partial tempura/tr opts [(keyword (listen [:language]))])
   args))
