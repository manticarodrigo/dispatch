(ns app.utils.google.maps)

(def transit-off
  {:elementType "all"
   :featureType "transit"
   :stylers  [{:visibility "off"}]})

(def poi-off
  {:elementType "all"
   :featureType "poi"
   :stylers  [{:visibility "off"}]})

(def administrative-off
  {:featureType "administrative" :stylers  [{:visibility "off"}]})

(def base-rules
  [poi-off
   transit-off
   administrative-off])

(def desaturate
  [{:elementType "labels"
    :featureType "road"
    :stylers  [{:visibility "on"}]}
   {:elementType "geometry.fill"
    :featureType "road"
    :stylers  [{:color "#000000"}  {:weight 1}]}
   {:elementType "geometry.stroke"
    :featureType "road"
    :stylers  [{:color "#000000"}  {:weight 0.8}]}
   {:featureType "landscape" :stylers  [{:color "#ffffff"}]}
   {:featureType "water" :stylers  [{:visibility "off"}]}
   {:featureType "transit" :stylers  [{:visibility "off"}]}
   {:elementType "labels" :stylers  [{:visibility "off"}]}
   {:elementType "labels.text" :stylers  [{:visibility "on"}]}
   {:elementType "labels.text.stroke" :stylers  [{:color "#ffffff"}]}
   {:elementType "labels.text.fill" :stylers  [{:color "#000000"}]}
   {:elementType "labels.icon" :stylers  [{:visibility "on"}]}])

(def simplify
  [[{:elementType "labels.text.fill"
     :featureType "administrative"
     :stylers  [{:color "#6195a0"}]}
    {:elementType "all"
     :featureType "landscape"
     :stylers  [{:color "#f2f2f2"}]}
    {:elementType "geometry.fill"
     :featureType "landscape"
     :stylers  [{:color "#ffffff"}]}
    {:elementType "geometry.fill"
     :featureType "poi.park"
     :stylers  [{:color "#e6f3d6"}  {:visibility "on"}]}
    {:elementType "all"
     :featureType "road"
     :stylers
     [{:saturation (- 100)}  {:lightness 45}
      {:visibility "simplified"}]}
    {:elementType "all"
     :featureType "road.highway"
     :stylers  [{:visibility "simplified"}]}
    {:elementType "geometry.fill"
     :featureType "road.highway"
     :stylers  [{:color "#f4d2c5"}  {:visibility "simplified"}]}
    {:elementType "labels.text"
     :featureType "road.highway"
     :stylers  [{:color "#4e4e4e"}]}
    {:elementType "geometry.fill"
     :featureType "road.arterial"
     :stylers  [{:color "#f4f4f4"}]}
    {:elementType "labels.text.fill"
     :featureType "road.arterial"
     :stylers  [{:color "#787878"}]}
    {:elementType "labels.icon"
     :featureType "road.arterial"
     :stylers  [{:visibility "off"}]}
    {:elementType "all"
     :featureType "water"
     :stylers  [{:color "#eaf6f8"}  {:visibility "on"}]}
    {:elementType "geometry.fill"
     :featureType "water"
     :stylers  [{:color "#eaf6f8"}]}]])

(defn- with-rules [style] (concat style base-rules))

(def map-styles
  {:desaturate (-> desaturate with-rules)
   :simplify (-> simplify with-rules)})
