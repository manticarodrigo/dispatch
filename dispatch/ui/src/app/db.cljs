(ns app.db
  (:require [app.config :as config]))

(def default-db {})

(def seed-db
  {:locale {:region config/REGION
            :language config/LANGUAGE}
   :origin {:lat 12.072344778464416, :lng -86.24555969727164}
   :location nil
   :stops [{:lat 12.103727746418377 :lng -86.2492445291432} ;; galerias
           {:lat 12.086721023651428 :lng -86.23377698736607} ;; vivian pellas
           {:lat 12.129391854083744 :lng -86.26494075705381} ;; metrocentro
           ]
  ;;  :route {:legs [{:distance {:text "4.6 km" :value 4573} :duration {:text "10 mins" :value 609} :address "Galerías Santo Domingo, Managua, Nicaragua" :location {:lat 12.1040642 :lng -86.2493812}}
  ;;                 {:distance {:text "3.7 km" :value 3728} :duration {:text "9 mins" :value 537} :address "Metrocentro Noreste, Pista Juan Pablo II, Managua 14005, Nicaragua" :location {:lat 12.1293441 :lng -86.26492420000001}}
  ;;                 {:distance {:text "7.4 km" :value 7359} :duration {:text "15 mins" :value 890} :address "Hospital Metropolitano Vivian Pellas, 3QP8+JJ7, Managua, Nicaragua" :location {:lat 12.0867044 :lng -86.23380159999999}}
  ;;                 {:distance {:text "4.6 km" :value 4553} :duration {:text "12 mins" :value 706} :address "24 Av. Sureste, Managua, Nicaragua" :location {:lat 12.0721546 :lng -86.24558259999999}}]
  ;;          :bounds {:lat 12.10125, :lng -86.24898000000002}}
   :search []})
