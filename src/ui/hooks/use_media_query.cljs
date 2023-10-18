(ns ui.hooks.use-media-query
  (:require ["react" :refer (useState useEffect)]))

(defn use-media-query [query]
  (let [[matches set-matches] (useState false)]
    (useEffect
     (fn []
       (let [media (.matchMedia js/window query)
             listener #(set-matches (. media -matches))]
         (when (not= matches (. media -matches))
           (listener))
         (.addListener media listener)
         #(.removeListener media listener)))
     #js[])
    matches))

;; tailwind default breakpoints

(def sm "(min-width: 640px)")
(def md "(min-width: 768px)")
(def lg "(min-width: 1024px)")
(def xl "(min-width: 1280px)")
(def xxl "(min-width: 1536px)")
