(ns ui.utils.i18n
  (:require
   [clojure.string :as s]
   [ui.subs :refer (listen)]
   [ui.utils.date :as d]))

(def ^:private dict
  {:en
   {:noun {:status "status"
           :never "never"
           :shipments "shipments"
           :vehicles "vehicles"
           :tasks "tasks"
           :date "date"
           :weight "weight"
           :volume "volume"
           :monitoring "monitoring"
           :planning "planning"}
    :verb {:create "create"
           :update "update"
           :archive "archive"
           :unarchive "unarchive"
           :upload "upload"
           :show "show"
           :view "view"
           :close "close"
           :optimize "optimize"}
    :adjective {:skipped "skipped"
                :succeeded "succeeded"
                :failed "failed"
                :pending "pending"
                :processing "processing"
                :archived "archived"
                :active "active"
                :inactive "inactive"
                :loading "loading"
                :new "new"
                :all "all"
                :none "none"
                :yes "yes"
                :no "no"
                :required "required"
                :optional "optional"}
    :view {:not-found "Page not found"
           :landing {:intro {:title (fn [] [:<> "Optimize your" [:br] "spend on travel"])
                             :subtitle (fn [] [:<> "Intelligently increase revenue" [:br] "and decrease costs for fleet operations."])
                             :cta "Get started for free"
                             :cta-note "First 100 optimized visits are on us."}
                     :optimization {:title "Use AI to automatically assign and optimize visits"
                                    :subtitle "Save time and money by letting us crunch the hard numbers."}
                     :constraints {:title (fn [] [:<> "Constrain visits" [:br] "to improve the solution"])
                                   :subtitle (fn [] [:<> "Optionally define volume, weight, time windows, " [:br] " and more to automatically generate an intelligent plan."])}
                     :monitoring {:title (fn [] [:<> "Solve problems on the road" [:br] "with real-time feedback"])
                                  :subtitle (fn [] [:<> "Detect issues and regenerate" [:br] "stop sequences, constraints, and more."])}}
           :register {:title "Register"
                      :login-link {:title "Already have an account?"
                                   :link "Login here."}}
           :login {:title "Login"
                   :register-link {:title "Need an account?"
                                   :link "Register here."}}
           :login-confirm {:title "Confirm login"
                           :return-link {:title "Didn't receive a code?"
                                         :link "Return to login."}}
           :analytics {:title "Analytics"
                       :charts {:revenue-per-gas-liter
                                {:title "Revenue per liter of gas"
                                 :subtitle "Last 30 days"}}}
           :task {:list {:title "Tasks"}
                  :create {:title "Create task"}
                  :update {:title "Update task"}}
           :agent {:list {:title "Agents"}
                   :create {:title "Create agent"}}
           :place {:list {:title "Places"}
                   :create {:title "Create place"}}
           :plan {:list {:title "Plans"}
                  :create {:title "Create plan"}
                  :optimize {:title "Optimize plan"}
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
                      :upload {:title "Upload shipments"}
                      :create {:title "Create shipment"}}
           :vehicle {:list {:title "Vehicles"}
                     :upload {:title "Upload vehicles"}
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
            :place "Place"
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
            :visit-duration "Visit duration (s)"
            :visit-date "Visit date"
            :visit-windows "Visit windows"
            :depot "Depot"
            :plan-date "Plan date"
            :plan-window "Plan window"
            :vehicles "Vehicles"
            :shipments "Shipments"
            :select-vehicles "Select vehicles"
            :select-shipments "Select shipments"
            :selected-vehicles (fn [[n]]
                                 (str "Selected " n " vehicles"))
            :selected-shipments (fn [[n]]
                                  (str "Selected " n " shipments"))
            :capacity-weight "Weight capacity (kg)"
            :capacity-volume "Volume capacity (m³)"
            :load-weight "Load weight (kg)"
            :load-volume "Load volume (m³)"
            :breaks "Breaks"
            :add-range "Add range"
            :duration "Duration"
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
                   :view-skipped-shipments "View skipped shipments"}
            :vehicle {:name "Name"
                      :weight "Weight capacity (kg)"
                      :volume "Volume capacity (m³)"}
            :shipment {:place "Place"
                       :weight "Weight (kg)"
                       :volume "Volume (m³)"
                       :duration "Duration (s)"
                       :windows "Visit windows"}
            :shipment-upload {:notes {:attach-csv "Attach a CSV file with the structure shown below."
                                      :column-names "Column names do not need to match, only the order."
                                      :external-id "External ID will be used to update and unarchive matching shipments."
                                      :external-place-id "External place ID is required and must match an existing external place ID."}
                              :columns {:external-id "External ID"
                                        :external-place-id "External place ID"
                                        :weight "Weight (kg)"
                                        :volume "Volume (m³)"
                                        :duration "Duration (s)"
                                        :start1 "First start (military)"
                                        :end1 "First end (military)"
                                        :start2 "Second start (military)"
                                        :end2 "Second end (military)"}}}

    :status {:all "All"
             :incomplete "Incomplete"
             :complete "Complete"
             :active "Active"
             :inactive "Inactive"
             :pending "Pending"
             :assigned "Assigned"
             :archived "Archived"
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

    :dropzone {:neutral "Drag and drop files here, or click to select files"
               :accepted "Drop the files here"
               :rejected "This file type is not supported"}

    :calendar {:previous-month "Previous month"
               :next-month "Next month"}
    :misc {:sign-out "Sign out"
           :loading "Loading"
           :empty-search "No results found"
           :back-to-home "Back to home"
           :leaving-from "Leaving from"}
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
           :vehicles "vehículos"
           :tasks "tareas"
           :date "fecha"
           :weight "peso"
           :volume "volumen"
           :monitoring "monitoreo"
           :planning "planificación"}
    :verb {:create "crear"
           :update "update"
           :archive "archivar"
           :unarchive "desarchivar"
           :upload "subir"
           :show "mostrar"
           :view "ver"
           :close "cerrar"
           :optimize "optimizar"}
    :adjective {:skipped "omitido"
                :succeeded "completado"
                :failed "fallido"
                :pending "pendiente"
                :processing "procesando"
                :archived "archivado"
                :active "activo"
                :inactive "inactivo"
                :loading "cargando"
                :new "nuevo"
                :all "todos"
                :none "ninguno"
                :yes "sí"
                :no "no"
                :required "requerido"
                :optional "opcional"}

    :view {:not-found "Página no encontrada"
           :landing {:intro {:title (fn [] [:<> "Optimiza tu" [:br] "gasto de viajes"])
                             :subtitle (fn [] [:<> "Aumenta tus ingresos" [:br] "y disminuye tus costos de operación."])
                             :cta "Empieza gratis ahora"
                             :cta-note "Las primeras 100 visitas optimizadas son gratis."}
                     :optimization {:title "Usa IA para asignar y optimizar visitas automáticamente"
                                    :subtitle "Ahorra tiempo y dinero al dejar que nosotros hagamos los cálculos."}
                     :constraints {:title (fn [] [:<> "Restringe visitas" [:br] "para mejorar la solución"])
                                   :subtitle (fn [] [:<> "Opcionalmente define volumen, peso, ventanas de tiempo, " [:br] " y más para generar un plan inteligente."])}
                     :monitoring {:title (fn [] [:<> "Resuelve problemas en el camino" [:br] "con retroalimentación en tiempo real"])
                                  :subtitle (fn [] [:<> "Detecta problemas y regenera" [:br] "secuencias de paradas, restricciones, y más."])}}
           :register {:title "Registrar"
                      :login-link {:title "¿Ya tienes una cuenta?"
                                   :link "Ingresar aquí."}}
           :login {:title "Ingresar"
                   :register-link {:title "¿Necesitas una cuenta?"
                                   :link "Registrar aquí."}}
           :login-confirm {:title "Confirmar ingreso"
                           :return-link {:title "No recibiste el código?"
                                         :link "Regresar a ingresar."}}
           :analytics {:title "Analítica"
                       :charts {:revenue-per-gas-liter {:title "Ingresos por litro de gasolina"
                                                        :subtitle "Ultimos 30 días"}}}
           :task {:list {:title "Tareas"}
                  :create {:title "Crear tarea"}
                  :update {:title "Actualizar tarea"}}
           :agent {:list {:title "Agentes"}
                   :create {:title "Crear agente"}}
           :place {:list {:title "Lugares"}
                   :create {:title "Crear lugar"}}
           :plan {:list {:title "Planes"}
                  :create {:title "Crear plan"}
                  :optimize {:title "Optimizar plan"}
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
                      :upload {:title "Subir envíos"}
                      :create {:title "Crear envío"}}
           :vehicle {:list {:title "Vehículos"}
                     :upload {:title "Subir vehículos"}
                     :create {:title "Crear vehículo"}}
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
            :place "Lugar"
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
            :weight "Capacidad de peso (kg)"
            :volume "Capacidad de volumen (m³)"
            :visit-duration "Duración de visita (s)"
            :visit-date "Fecha de visita"
            :visit-windows "Ventanas de visita"
            :depot "Depósito"
            :plan-date "Fecha de plan"
            :plan-window "Ventana de plan"
            :vehicles "Vehículos"
            :shipments "Envíos"
            :select-vehicles "Selecciona vehículos"
            :select-shipments "Selecciona envíos"
            :selected-vehicles (fn [[n]]
                                 (str n " vehículos seleccionados"))
            :selected-shipments (fn [[n]]
                                  (str n " envíos seleccionados"))
            :capacity-weight "Capacidad de peso (kg)"
            :capacity-volume "Capacidad de volumen (m³)"
            :load-weight "Peso de carga (kg)"
            :load-volume "Volumen de carga (m³)"
            :add-range "Agregar rango"
            :breaks "Descansos"
            :duration "Duración"
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
                   :view-skipped-shipments "Ver envíos omitidos"}
            :vehicle {:name "Nombre"
                      :weight "Capacidad de peso (kg)"
                      :volume "Capacidad de volumen (m³)"}
            :shipment {:place "Lugar"
                       :weight "Peso (kg)"
                       :volume "Volumen (m³)"
                       :duration "Duración (s)"
                       :windows "Ventanas de visita"}
            :shipment-upload {:notes {:attach-csv "Adjuntar CSV con la estructura mostrada abajo."
                                      :column-names "Los nombres de las columnas no necesitan coincidir, solo el orden."
                                      :external-id "ID externo será usado para actualizar y desarchivar envíos que coincidan."
                                      :external-place-id "ID externo de lugar es requerido y debe coincidir con un ID externo de lugar existente."}
                              :columns {:external-id "ID externo"
                                        :external-place-id "ID externo de lugar"
                                        :weight "Peso (kg)"
                                        :volume "Volumen (m³)"
                                        :duration "Duración (s)"
                                        :start1 "Primer inicio (militar)"
                                        :end1 "Primer fin (militar)"
                                        :start2 "Segundo inicio (militar)"
                                        :end2 "Segundo fin (militar)"}}}

    :status {:all "Todos"
             :incomplete "Incompleto"
             :complete "Completo"
             :active "Activo"
             :inactive "Inactivo"
             :pending "Pendiente"
             :assigned "Asignado"
             :archived "Archivado"
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

    :dropzone {:neutral "Arrastra y suelta los archivos aquí"
               :accepted "Suelta los archivos aqui"
               :rejected "Este tipo de archivo no es soportado"}

    :misc {:sign-out "Cerrar sesión"
           :loading "Cargando"
           :empty-search "No se han encontrado resultados"
           :back-to-home "Regresar a inicio"
           :leaving-from "Salida desde"}
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

(defn tr [& args]
  (let [[[kw] & params] args
        lang (listen [:language])
        strings (-> kw str (s/replace-first ":" "") (s/split #"[./]"))
        keywords (map keyword strings)
        path (cons (keyword lang) keywords)
        val (get-in dict path)]
    (if (fn? val)
      (apply val params)
      val)))
