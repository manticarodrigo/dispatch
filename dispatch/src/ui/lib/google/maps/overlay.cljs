(ns ui.lib.google.maps.overlay
  (:require [cljs-bean.core :refer (->js)]))

(def pulsating-dot
  "<div class='flex absolute -translate-y-[50%]'>
     <div class='absolute animate-ping h-full w-full rounded-full bg-blue-400 opacity-75'></div>
     <div class='rounded-full h-4 w-4 bg-blue-500'></div>
   </div>")

(def title-header
  "<div class='relative flex flex-col items-center'>
    <div class='absolute -translate-y-[100%] -mt-2 text-sm text-neutral-900 whitespace-nowrap'>")

(defn add-title [title content]
  (str title-header title "</div>" content "</div>"))

(defn- create-overlay [^js gmap content]
  (let [^js OverlayClass (fn
                           [gmap content]
                           (this-as
                            ^js this
                            (.setMap this gmap)
                            (set! (.-content this) content)))]
    (set! (.. OverlayClass -prototype) (js/google.maps.OverlayView.))
    (set! (.. OverlayClass -prototype -update)
          (fn [title position]
            (this-as
             ^js this
             (let [container (.. this -container)
                   content (.. this -content)]
               (if container
                 (do (set! (.. this -title) title)
                     (set! (.. this -position) position)
                     (set! (.. container -innerHTML) (add-title title content))
                     (.draw this))
                 (js/setTimeout #(.update this title position)))))))
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
             (let [container (.createElement js/document "div")]
               (set! (.. container -style -display) "none")
               (set! (.. container -style -position) "absolute")
               (set! (.. this -container) container)
               (.appendChild (-> this .getPanes .-floatPane) container)))))
    (set! (.. OverlayClass -prototype -onRemove)
          (fn []
            (this-as
             ^js this
             (let [container (.. this -container)]
               (.removeChild (.-parentNode container) container)
               (set! (.. this -container) nil)))))
    (OverlayClass. gmap content)))

(defn clear-overlay [^js overlay]
  (.setMap overlay nil))

(defn set-overlays [^js gmap locations]
  (mapv (fn [{:keys [title position]}]
          (let [overlay (create-overlay gmap pulsating-dot)]
            (.update overlay title (->js position))
            overlay))
        locations))

(defn clear-overlays [overlays]
  (doseq [overlay overlays]
    (clear-overlay overlay)))
