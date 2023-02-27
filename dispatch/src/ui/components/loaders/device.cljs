(ns ui.components.loaders.device
  (:require
   [react]
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [ui.lib.device :refer (get-device-info)]
   [ui.components.loaders.base :rename {loader base-loader}]))

(defn loader [& children]
  (let [!loaded (r/atom false)]
    (fn []

      (react/useEffect
       (fn []
         (-> (get-device-info)
             (.then (fn [device]
                      (dispatch [:device device])
                      (reset! !loaded true))))
         #())
       #js[])

      (if @!loaded
        (into [:<>] children)
        [base-loader]))))
