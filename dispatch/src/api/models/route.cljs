(ns api.models.route
  (:require
   [promesa.core :as p]
   [goog.object :as gobj]
   [api.util.prisma :as prisma]
   [api.filters.core :as filters]
   [api.models.user :refer (logged-in-user)]))

(defn create [^js context {:keys [seatId startAt addressIds route]}]
  (p/let [^js user (logged-in-user context)]
    (prisma/create!
     (.. context -prisma -route)
     {:data {:user {:connect {:id (.-id user)}}
             :seat {:connect {:id seatId}}
             :stops {:create (mapv (fn [[idx id]]
                                     {:order idx
                                      :address {:connect {:id id}}})
                                   (map-indexed vector addressIds))}
             :route route
             :startAt startAt}
      :include {:seat true
                :stops {:include {:address true}
                        :orderBy {:order "asc"}}}})))

(defn find-all [^js context {:keys [filters]}]
  (p/-> (logged-in-user
         context
         {:include
          {:routes
           {:where (filters/route filters)
            :orderBy {:startAt "asc"}
            :include {:seat true
                      :stops {:include {:address true}
                              :orderBy {:order "asc"}}}}}})
        (gobj/get "routes")))

(defn find-unique [^js context {:keys [id]}]
  (p/-> (logged-in-user
         context
         {:include
          {:routes
           {:where {:id id}
            :include {:seat {:include {:location true}}
                      :stops {:include {:address true}
                              :orderBy {:order "asc"}}}}}})
        (gobj/get "routes")
        first))

(defn find-by-address [^js context {:keys [id filters]}]
  (let [{:keys [start end status]} filters]
    (p/-> (logged-in-user
           context
           ;; TODO: move to address model
           {:include
            {:routes
             {:where {:AND [{:stops {:some {:address {:id id}}}}
                            {:stops (condp = status
                                      "INCOMPLETE" {:some {:arrivedAt {:equals nil}}}
                                      "COMPLETE" {:every {:arrivedAt {:not nil}}}
                                      nil {})}
                            (if (and start end) {:startAt {:gte start}} {})
                            (if (and start end) {:startAt {:lte end}} {})]}
              :orderBy {:startAt "asc"}
              :include {:seat true
                        :stops {:include {:address true}
                                :orderBy {:order "asc"}}}}}})
          (gobj/get "routes"))))
