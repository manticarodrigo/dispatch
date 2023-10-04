(ns ui.components.loaders.device
  (:require ["react" :refer (useState useEffect)]
            [re-frame.core :refer (dispatch)]
            [ui.lib.device :refer (get-device-info)]
            [ui.components.loaders.base :rename {loader base-loader}]))

(defn loader [& children]
  (let [[loaded set-loaded] (useState false)]
    (useEffect
     (fn []
       (-> (get-device-info)
           (.then (fn [device]
                    (dispatch [:device device])
                    (set-loaded true))))
       #())
     #js[])
    (if loaded
      (into [:<>] children)
      [base-loader])))
