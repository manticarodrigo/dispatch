(ns app.db)

(def default-db
  {:locale {:language "en" :region "US"}
   :origin nil
   :location nil
   :stops [{:lat 12.103727746418377 :lng -86.2492445291432} ;; galerias
           {:lat 12.086721023651428 :lng -86.23377698736607} ;; vivian pellas
           {:lat 12.129391854083744 :lng -86.26494075705381} ;; metrocentro
           ]
   :route []})
