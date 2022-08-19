(ns app.hooks.use-map
  (:require
   [react :refer (useEffect)]
   [re-frame.core :refer (dispatch)]
   ["@googlemaps/js-api-loader" :refer (Loader)]
   [clojure.core :refer (atom)]
   [cljs.core.async :refer (go)]
   [cljs.core.async.interop :refer-macros (<p!)]
   [app.config :as config]
   [app.subs :refer (listen)]
   [app.utils.google.maps.styles :refer (styles)]
   [app.utils.google.maps.overlay :refer (create-overlay)]))

(set! *warn-on-infer* false)

(defonce ^:private initial-zoom 2)

(defonce ^:private !loader (atom nil))
(defonce ^:private !el (atom nil))
(defonce ^:private !map (atom nil))
(defonce ^:private !route-markers (atom []))
(defonce ^:private !location-overlay (atom nil))
(defonce ^:private !autocomplete-service (atom nil))
(defonce ^:private !places-service (atom nil))
(defonce ^:private !directions-service (atom nil))
(defonce ^:private !directions-renderer (atom nil))

(defn- load-map []
  (reset!
   !loader
   (Loader.
    (clj->js
     {:id "google-maps-script"
      :apiKey config/GOOGLE_MAPS_API_KEY
      :version "weekly"
      :libraries ["places"]
      :language (listen [:locale/language])
      :region (listen [:locale/region])})))
  (.load @!loader))

(defn- create-map []
  (js/Promise.
   (fn [resolve _]
     (go
       (<p! (load-map))
       (resolve
        (js/google.maps.Map.
         @!el (clj->js {:center {:lat 0 :lng 0}
                        :zoom initial-zoom
                        :disableDefaultUI true
                        :styles (:desaturate styles)})))))))

(defn- create-autocomplete-service []
  (js/google.maps.places.AutocompleteService.))

(defn- create-places-service []
  (js/google.maps.places.PlacesService. @!map))

(defn- create-directions-service []
  (js/google.maps.DirectionsService.))

(defn- create-directions-renderer []
  (let [directions-renderer (js/google.maps.DirectionsRenderer.)]
    (.setMap directions-renderer @!map)
    directions-renderer))

(defn- create-lat-lng [lat lng]
  (js/google.maps.LatLng. lat lng))

(defn- get-lat-lng [location]
  (let [{lat :lat lng :lng} location]
    (create-lat-lng lat lng)))

(defn- parse-lat-lng [^LatLng lat-lng]
  (let [lat (-> lat-lng .lat)
        lng (-> lat-lng .lng)]
    {:lat lat :lng lng}))

(defn- create-route-request [origin stops]
  (let [origin (get-lat-lng origin)
        waypoints (map (fn [stop] {:location (get-lat-lng stop) :stopover true}) stops)]
    (clj->js {:origin origin
              :destination origin
              :waypoints waypoints
              :optimizeWaypoints true
              :travelMode "DRIVING"})))

(defn- parse-leg [leg]
  (let [{:keys [distance duration end_address]} (js->clj leg :keywordize-keys true)]
    {:distance distance :duration duration :address end_address}))

(defn- parse-legs [response]
  (some-> response (.-routes) (first) (.-legs)))

(defn- parse-route [legs]
  (mapv parse-leg legs))

(defn- create-marker [opts]
  (js/google.maps.Marker.
   (clj->js (assoc opts :map @!map))))

(defn- clear-markers! []
  (doseq [marker @!route-markers] (.setMap marker nil))
  (reset! !route-markers []))

(defn- create-route-markers [legs]
  (clear-markers!)
  (doseq [[idx leg] (map-indexed vector legs)]
    (let [position (.-end_location leg)
          title (.-end_address leg)
          label (str (+ 1 idx))
          marker (create-marker
                  {:position position
                   :title title
                   :label {:text label :color "white"}
                   :icon {:url "/images/svg/pin.svg"
                          :scaledSize (js/google.maps.Size. 30 30)}
                   :zIndex idx})]
      (swap! !route-markers conj marker))))



(def ^:private pulse-html
  "<span class='relative'>
     <span class='absolute -translate-x-2 -translate-y-2 flex'>
       <span class='animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75'></span>
       <span class='relative inline-flex rounded-full h-4 w-4 bg-blue-500'></span>
     </span>
   </span>")

(defn- create-location-overlay []
  (create-overlay js/google @!map pulse-html))

(defn- handle-route-response! [response]
  (let [renderer @!directions-renderer
        legs (parse-legs response)
        route (parse-route legs)]
    (.setOptions renderer #js{:suppressMarkers true})
    (.setDirections renderer response)
    (create-route-markers legs)
    (dispatch [:route/set route])))

(defn- calc-route! [location stops]
  (let [service @!directions-service
        request (create-route-request location stops)]
    (.route service request
            (fn [response status]
              (when (= status "OK")
                (handle-route-response! response))))))

(defn- search-places! [text]
  (let [service @!autocomplete-service]
    (.getQueryPredictions
     service
     #js{:input text}
     (fn [results status]
       (when (or (= status js/google.maps.places.PlacesServiceStatus.OK)
                 (count results))
         (dispatch [:search/set (js->clj results :keywordize-keys true)]))))))

(defn- set-origin! [place-id]
  (let [service @!places-service]
    (.getDetails
     service
     (clj->js {:placeId place-id,
               :fields ["geometry"]})
     (fn [place status]
       (when (= status js/google.maps.places.PlacesServiceStatus.OK)
         (dispatch
          [:origin/set
           (parse-lat-lng (-> place .-geometry .-location))]))))))

(defn use-map []
  (let [location (listen [:location])
        origin (listen [:origin])
        stops (listen [:stops])]

    (useEffect
     (fn []
       (go
         (reset! !map (<p! (create-map)))
         (reset! !autocomplete-service (create-autocomplete-service))
         (reset! !places-service (create-places-service))
         (reset! !directions-service (create-directions-service))
         (reset! !directions-renderer (create-directions-renderer))
         (reset! !location-overlay (create-location-overlay)))
       #())
     #js[])

    (useEffect
     (fn []
       (when (and @!map location)
         (when-let [overlay @!location-overlay]
           (.update overlay (get-lat-lng location))))
       (fn []))
     #js[location])

    (useEffect
     (fn []
       (when (and @!map origin)
         (calc-route! origin stops))
       (fn []))
     #js[origin])

    {:ref !el :map @!map :search search-places! :origin set-origin!}))
