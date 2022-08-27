(ns app.utils.google.maps.overlay)

(defonce ^:private !location-overlay (atom nil))

(defn- class
  [map content]
  (this-as
   ^js this
   (.setMap this map)
   (set! (.-content this) content)))

(defn- on-draw []
  (this-as
   ^js this
   (let [container (.. this -container)
         position (.. this -position)
         projection (.getProjection this)
         pos (when position (.fromLatLngToDivPixel projection position))]
     (when position
       (set! (.. container -style -display) "block")
       (set! (.. container -style -left) (str (.-x pos) "px"))
       (set! (.. container -style -top) (str (.-y pos) "px"))))))

(defn- on-add []
  (this-as
   ^js this
   (let [container (.createElement js/document "div")
         content (.. this -content)]
     (set! (.. container -innerHTML) content)
     (set! (.. container -style -display) "none")
     (set! (.. container -style -position) "absolute")
     (set! (.. this -container) container)
     (.appendChild (-> this .getPanes .-floatPane) container))))

(defn- on-update [position]
  (this-as
   ^js this
   (let [container (.. this -container)]
     (when-not container (.addContainer this))
     (set! (.. this -position) position)
     (.draw this))))

(defn- on-remove []
  (this-as
   ^js this
   (let [container (.. this -container)]
     (.removeChild container (.-parentNode container))
     (set! (.. this -container) nil))))

;; (defn- method [name] 
;;  (set! (get (.. class -prototype) name) on-draw))


(defn- create-overlay [google map content]
  (let [^js instance class]
    (set! (.. instance -prototype) (google.maps.OverlayView.))
    (set! (.. instance -prototype -draw) on-draw)
    (set! (.. instance -prototype -onAdd) on-add)
    (set! (.. instance -prototype -update) on-update)
    (set! (.. instance -prototype -onRemove) on-remove)
    (instance. map content)))

(defn update-overlay [latlng]
  (when-let [^js instance @!location-overlay]
    (.update instance latlng)))

(def ^:private html
  "<span class='relative'>
     <span class='absolute -translate-x-2 -translate-y-2 flex'>
       <span class='animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75'></span>
       <span class='relative inline-flex rounded-full h-4 w-4 bg-blue-500'></span>
     </span>
   </span>")

(defn init-overlay [gmap]
  (reset! !location-overlay (create-overlay js/google gmap html)))
