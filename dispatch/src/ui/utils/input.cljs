(ns ui.utils.input
  (:import [goog.async Debouncer]))

(defn debounce [f interval]
  (let [dbnc (Debouncer. f interval)]
    ;; we use apply here to support functions of various arities
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))
