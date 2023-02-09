(ns ui.lib.google.maps.overlay
  (:require [cljs-bean.core :refer (->js)]))

(def pulsating-dot
  "<span class='relative'>
     <span class='absolute -translate-x-2 -translate-y-2 flex'>
       <span class='animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75'></span>
       <span class='relative inline-flex rounded-full h-4 w-4 bg-blue-500'></span>
     </span>
   </span>")

(def title-header
  "<div class='flex flex-col items-center'>
    <div class='text-sm text-neutral-900 -translate-y-2'>")

(defn add-title [title content]
  (str title-header title "</div>" content "</div>"))

(defn- create-overlay [map content]
  (let [^js OverlayClass (fn
                           [map content]
                           (this-as
                            ^js this
                            (.setMap this map)
                            (set! (.-content this) content)))]
    (set! (.. OverlayClass -prototype) (js/google.maps.OverlayView.))
    (set! (.. OverlayClass -prototype -update)
          (fn [title position]
            (this-as
             ^js this
             (let [container (.. this -container)]
               (if container
                 (do (set! (.. this -title) title)
                     (set! (.. this -position) position)
                     (.draw this))
                 (js/setTimeout #(.update this title position)))))))
    (set! (.. OverlayClass -prototype -draw)
          (fn []
            (this-as
             ^js this
             (let [container (.. this -container)
                   content (.. this -content)
                   title (.. this -title)
                   position (.. this -position)
                   projection (.getProjection this)
                   pos (when position (.fromLatLngToDivPixel projection position))]
               (when position
                 (set! (.. container -innerHTML) (add-title title content))
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
    (set! (.. OverlayClass -prototype -onRemove)
          (fn []
            (this-as
             ^js this
             (let [container (.. this -container)]
               (.removeChild (.-parentNode container) container)
               (set! (.. this -container) nil)))))
    (OverlayClass. map content)))

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
