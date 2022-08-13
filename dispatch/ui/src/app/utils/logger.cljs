(ns app.utils.logger)

(defn log
  [& args]
  (apply js/console.log args))

(defn error
  [& args]
  (apply js/console.error args))
