(ns ui.lib.router
  (:require
   [react-router-dom
    :refer (BrowserRouter)]))

(defn browser-router [& children]
  [:> BrowserRouter
   (into [:<>] children)])
