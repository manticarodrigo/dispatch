(ns app.utils.google.maps.overlay)

(set! *warn-on-infer* false)

(defn- class
  [map content]
  (this-as
   this
   (.setMap this map)
   (set! (.-content this) content)))

(defn- on-draw []
  (this-as
   this
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
   this
   (let [container (.createElement js/document "div")
         content (.. this -content)]
     (set! (.. container -innerHTML) content)
     (set! (.. container -style -display) "none")
     (set! (.. container -style -position) "absolute")
     (set! (.. this -container) container)
     (.appendChild (-> this .getPanes .-floatPane) container))))

(defn- on-update [position]
  (this-as
   this
   (let [container (.. this -container)]
     (when-not container (.addContainer this))
     (set! (.. this -position) position)
     (.draw this))))

(defn- on-remove []
  (this-as
   this
   (let [container (.. this -container)]
     (.removeChild container (.-parentNode container))
     (set! (.. this -container) nil))))

(defn create-overlay [google map content]
  (let [Overlay class]
    (set! (.. Overlay -prototype) (google.maps.OverlayView.))
    (set! (.. Overlay -prototype -draw) on-draw)
    (set! (.. Overlay -prototype -onAdd) on-add)
    (set! (.. Overlay -prototype -update) on-update)
    (set! (.. Overlay -prototype -onRemove) on-remove)
    (Overlay. map content)))
