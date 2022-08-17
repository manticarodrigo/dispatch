(ns app.views.route.nav.container
  (:require [app.utils.string :refer (class-names)]))

(defn container [class children]
  [:section {:class
             (class-names
              class
              "z-10 relative flex-none"
              "w-full lg:w-[450px] lg:h-full"
              "text-white bg-black")}
   children])
