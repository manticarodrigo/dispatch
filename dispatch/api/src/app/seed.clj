(ns app.seed)

(defn- latlng [lat lng]
  {:location/lat lat :location/lng lng})

(defn- user [id username first last location]
  {:db/id id
   :user/username username
   :user/firstname first
   :user/lastname last
   :user/location location})

(defn- item [name]
  {:item/name name})

(defn- items [names]
  (map #(item %) names))

(defn- order [id customer items]
  {:db/id id
   :order/customer customer
   :order/items items})

(defn- route [id driver orders origin destination]
  {:db/id id
   :route/driver driver
   :route/orders orders
   :route/origin origin
   :route/destination destination})

(def ^:private home (latlng 12.072344778464416 -86.24555969727164))
(def ^:private galerias (latlng 12.103727746418377 -86.2492445291432))
(def ^:private vivian-pellas (latlng 12.086721023651428 -86.23377698736607))
(def ^:private metrocentro (latlng 12.129391854083744 -86.26494075705381))

(def seed-tx
  [(route "temp-route"
          (user "temp-driver"
                "driver"
                "Driver"
                "Doe"
                home)
          [(order "temp-order-1"
                  (user "temp-customer-1"
                        "customer-1"
                        "Customer"
                        "First"
                        galerias)
                  (items ["Apple" "Banana"]))
           (order "temp-order-2"
                  (user "temp-customer-2"
                        "customer-2"
                        "Customer"
                        "Second"
                        vivian-pellas)
                  (items ["Orange" "Pear"]))
           (order "temp-order-3"
                  (user "temp-customer-3"
                        "customer-3"
                        "Customer"
                        "Third"
                        metrocentro)
                  (items ["Kiwi" "Strawberry"]))]
          home
          home)])
