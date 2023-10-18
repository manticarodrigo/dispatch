(ns ui.lib.google.maps.overlay
  (:require [cljs-bean.core :refer (->js)]
            [reagent.dom.server :refer (render-to-string)]
            [ui.utils.string :refer (class-names)]
            [ui.utils.location :refer (position-to-lat-lng)]))

(defn content [{:keys [title heading]}]
  (render-to-string
   [:div {:class "relative flex flex-col items-center"}
    [:div {:class "absolute -translate-y-[100%] -mt-2 text-sm text-neutral-900 whitespace-nowrap"}
     title]
    [:div {:class "flex absolute -translate-y-[50%]"}
     [:div {:class "absolute animate-ping h-full w-full rounded-full bg-blue-400 opacity-75"}]
     [:div {:class "rounded-full h-4 w-4 bg-blue-500"}]
     (when (some? heading)
       [:div {:class "absolute h-4 w-4"
              :style {:transform (str "rotate(" (or heading 0) "deg)")}}
        [:div {:class (class-names
                       "w-0 h-0"
                       "absolute right-[50%] translate-x-[50%] scale-75 bottom-[calc(100%_+_0.1rem)]"
                       "border-l-8 border-l-solid border-l-transparent"
                       "border-r-8 border-r-solid border-r-transparent"
                       "border-b-8 border-b-solid border-b-blue-500")}]])]]))

(defn- create-overlay [^js gmap]
  (let [^js OverlayClass (fn
                           [gmap]
                           (this-as
                            ^js this
                            (.setMap this gmap)))]
    (set! (.. OverlayClass -prototype) (js/google.maps.OverlayView.))
    (set! (.. OverlayClass -prototype -update)
          (fn [title position]
            (this-as
             ^js this
             (let [container (.. this -container)]
               (if container
                 (do (set! (.. this -position) position)
                     (set! (.. container -innerHTML) (content
                                                      {:title title
                                                       :heading (:heading position)}))
                     (.draw this))
                 (js/setTimeout #(.update this title position)))))))
    (set! (.. OverlayClass -prototype -draw)
          (fn []
            (this-as
             ^js this
             (let [container (.. this -container)
                   position (.. this -position)
                   projection (.getProjection this)
                   pos (when position
                         (.fromLatLngToDivPixel
                          projection
                          (-> position
                              position-to-lat-lng
                              ->js)))]
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
    (OverlayClass. gmap)))

(defn clear-overlay [^js overlay]
  (.setMap overlay nil))

(defn set-overlays [^js gmap locations]
  (mapv (fn [{:keys [title position]}]
          (let [overlay (create-overlay gmap)]
            (.update overlay title position)
            overlay))
        locations))

(defn clear-overlays [overlays]
  (doseq [overlay overlays]
    (clear-overlay overlay)))
