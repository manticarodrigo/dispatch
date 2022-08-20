(ns app.utils.google.maps.serializer)

(set! *warn-on-infer* false)

(defn parse-lat-lng [lat-lng]
  (let [lat (.lat lat-lng)
        lng (.lng lat-lng)]
    {:lat lat :lng lng}))

(defn- parse-bounds [bounds]
  (let [{north :lat east :lng} (-> bounds .getNorthEast parse-lat-lng)
        {south :lat west :lng} (-> bounds .getSouthWest parse-lat-lng)]
    {:north north :east east :south south :west west}))

(defn- parse-path [path]
  (mapv parse-lat-lng path))

(defn- parse-leg [leg]
  (let [{:keys [distance
                duration
                end_address
                end_location]} (js->clj leg :keywordize-keys true)]
    {:distance distance
     :duration duration
     :address end_address
     :location (parse-lat-lng end_location)}))

(defn parse-route [response]
  (let [route (some-> response .-routes first)
        legs (mapv parse-leg (.-legs route))
        bounds (parse-bounds (.-bounds route))
        path (parse-path (.-overview_path route))]
    {:legs legs :bounds bounds :path path}))

(defn parse-place [place]
  (parse-lat-lng (-> place .-geometry .-location)))
