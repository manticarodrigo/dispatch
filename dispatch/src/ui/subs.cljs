(ns ui.subs
  (:require [re-frame.core :as rf]))

(defn listen [query-vector]
  @(rf/subscribe query-vector))


(rf/reg-sub :session #(:session %))
(rf/reg-sub :locale #(:locale %))
(rf/reg-sub :locale/language #(-> % :locale :language))
(rf/reg-sub :locale/region #(-> % :locale :region))

(rf/reg-sub :search #(:search %))
(rf/reg-sub :origin #(:origin %))
(rf/reg-sub :location #(:location %))

(rf/reg-sub :route #(-> % :route))
(rf/reg-sub :route/legs #(some-> % :route :legs))
(rf/reg-sub :route/bounds #(some-> % :route :bounds))
(rf/reg-sub :route/path #(some-> % :route :path))

(defn- convert-units [db key denominator]
  (let [legs (some-> db :route :legs)
        units (mapv #(key %) legs)
        sum (reduce + units)]
    (js/Math.round (/ sum denominator))))

(defn- get-minutes [db] (convert-units db :duration 60))
(defn- get-kms [db] (convert-units db :distance 1000))

(rf/reg-sub :route/minutes get-minutes)
(rf/reg-sub :route/kilometers get-kms)
