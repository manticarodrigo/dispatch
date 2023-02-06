(ns ui.components.loader
  (:require
   [react]
   [reagent.core :as r]
   [re-frame.core :refer (dispatch)]
   [ui.lib.device :refer (get-device-info)]
   [ui.components.icons.dispatch :rename {dispatch dispatch-icon}]))

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
        [:div {:class "flex justify-center items-center h-full w-full"}
         [:div {:class "animate-pulse"}
          [dispatch-icon {:width 36 :height 36}]]]))))
