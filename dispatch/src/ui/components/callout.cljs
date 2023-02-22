(ns ui.components.callout
  (:require [react-feather :rename {AlertCircle SuccessIcon
                                    AlertTriangle WarningIcon
                                    AlertOctagon ErrorIcon
                                    Info InfoIcon}]
            [ui.utils.string :refer (class-names)]))

(defn callout [type message]
  [:span
   {:class
    (class-names
     "flex items-center"
     "my-2 p-2 rounded border text-sm"
     (case type
       "success" "border-green-600 text-green-100 bg-green-900"
       "warning" "border-amber-600 text-amber-100 bg-amber-900"
       "error" "border-red-600 text-red-100 bg-red-900"
       "border-blue-600 text-blue-100 bg-blue-900"))}
   [:> (case type
         "success" SuccessIcon
         "warning" WarningIcon
         "error" ErrorIcon
         InfoIcon)
    {:class "flex-shrink-0 mr-2 w-4 h-4"}]
   message])
