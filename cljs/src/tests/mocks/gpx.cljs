(ns tests.mocks.gpx)

(def header "
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<gpx version=\"1.1\">
  <trk>
    <trkseg>
")

(def footer "
    </trkseg>
  </trk>
</gpx>
")

(defn to-gpx [coords]
  (let [trackpoints (map (fn [{:keys [lat lng]}]
                           (str "<trkpt lat=\"" lat "\" lon=\"" lng "\"></trkpt>"))
                         coords)]
    (str header (apply str trackpoints) footer)))
