(ns ui.lib.google.maps.overlay
  (:require [cljs-bean.core :refer (->js)]))

(defonce ^:private !location-overlay (atom nil))

(defn- create-overlay [map content]
  (let [^js OverlayClass (fn
                           [map content]
                           (this-as
                            ^js this
                            (.setMap this map)
                            (set! (.-content this) content)))]
    (set! (.. OverlayClass -prototype) (js/google.maps.OverlayView.))
    (set! (.. OverlayClass -prototype -draw)
          (fn []
            (this-as
             ^js this
             (let [container (.. this -container)
                   position (.. this -position)
                   projection (.getProjection this)
                   pos (when position (.fromLatLngToDivPixel projection position))]
               (when position
                 (set! (.. container -style -display) "block")
                 (set! (.. container -style -left) (str (.-x pos) "px"))
                 (set! (.. container -style -top) (str (.-y pos) "px")))))))
    (set! (.. OverlayClass -prototype -onAdd)
          (fn []
            (this-as
             ^js this
             (let [container (.createElement js/document "div")
                   content (.. this -content)]
               (set! (.. container -innerHTML) content)
               (set! (.. container -style -display) "none")
               (set! (.. container -style -position) "absolute")
               (set! (.. this -container) container)
               (.appendChild (-> this .getPanes .-floatPane) container)))))
    (set! (.. OverlayClass -prototype -update)
          (fn [position]
            (this-as
             ^js this
             (let [container (.. this -container)]
               (when-not container (.addContainer this))
               (set! (.. this -position) position)
               (.draw this)))))
    (set! (.. OverlayClass -prototype -onRemove)
          (fn []
            (this-as
             ^js this
             (let [container (.. this -container)]
               (.removeChild container (.-parentNode container))
               (set! (.. this -container) nil)))))
    (OverlayClass. map content)))

(defn update-overlay [latlng]
  (when-let [^js instance @!location-overlay]
    (.update instance (->js latlng))))

(def ^:private html
  "<span class='relative'>
     <span class='absolute -translate-x-2 -translate-y-2 flex'>
       <span class='animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75'></span>
       <span class='relative inline-flex rounded-full h-4 w-4 bg-blue-500'></span>
     </span>
   </span>")

(defn init-overlay [gmap]
  (reset! !location-overlay (create-overlay gmap html)))
