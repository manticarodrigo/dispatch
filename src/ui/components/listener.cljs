(ns ui.components.listener
  (:require ["react" :refer (useEffect)]
            ["@capacitor/app" :refer (App)]
            [ui.lib.router :refer (use-navigate)]))

(defn listener [& children]
  ;; (let [navigate (use-navigate)]
  ;;   (useEffect
  ;;    (fn []
  ;;      (-> App
  ;;          (.addListener
  ;;           "appUrlOpen"
  ;;           (fn [event]
  ;;             (let [url (js/URL. (.-url event))
  ;;                   pathname (.-pathname url)
  ;;                   search (.-search url)
  ;;                   slug (str pathname search)]
  ;;               (navigate slug)))))
  ;;      #(.removeAllListeners App))
  ;;    #js[])
  ;;   (into [:<>] children))
  (into [:<>] children))
