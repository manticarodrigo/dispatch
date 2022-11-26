(ns ui.db
  (:require [ui.config :as config]
            [ui.utils.session :refer (get-session)]))

(def default-db {})

(def seed-db
  {:session (get-session)
   :locale {:region config/REGION
            :language config/LANGUAGE}
   :origin {:lat 12.072344778464416, :lng -86.24555969727164}
   :location nil
   :stops [
        ;;    {:lat 12.103727746418377 :lng -86.2492445291432} ;; galerias
        ;;    {:lat 12.086721023651428 :lng -86.23377698736607} ;; vivian pellas
        ;;    {:lat 12.129391854083744 :lng -86.26494075705381} ;; metrocentro
           ]
   :route {:legs [{:distance 4573 :duration 609 :address "Galerías Santo Domingo, Managua, Nicaragua" :location {:lat 12.1040642 :lng -86.2493812}}
                  {:distance 3728 :duration 537 :address "Metrocentro Noreste, Pista Juan Pablo II, Managua 14005, Nicaragua" :location {:lat 12.1293441 :lng -86.26492420000001}}
                  {:distance 7359 :duration 890 :address "Hospital Metropolitano Vivian Pellas, 3QP8+JJ7, Managua, Nicaragua" :location {:lat 12.0867044 :lng -86.23380159999999}}
                  {:distance 4553 :duration 706 :address "24 Av. Sureste, Managua, Nicaragua" :location {:lat 12.0721546 :lng -86.24558259999999}}]
           :bounds {:north 12.130350000000002, :east -86.23115000000001, :south 12.07215, :west -86.26681}}
   :search []})
