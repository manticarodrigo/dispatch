(ns ui.components.panel
  (:require
   [ui.utils.string :refer (class-names)]))

(defn panel [class & children]
  [:section {:class
             (class-names
              class
              "flex-none"
              "w-full lg:w-[450px] h-full overflow-y-auto")}
   (into [:<>] children)])
