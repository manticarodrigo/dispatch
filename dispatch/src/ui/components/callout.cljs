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
       "success" "border-green-700 text-green-50 bg-green-900"
       "warning" "border-amber-700 text-amber-50 bg-amber-900"
       "error" "border-red-700 text-red-50 bg-red-900"
       "border-blue-700 text-blue-50 bg-blue-900"))}
   [:> (case type
         "success" SuccessIcon
         "warning" WarningIcon
         "error" ErrorIcon
         InfoIcon)
    {:class "flex-shrink-0 mr-2 w-4 h-4"}]
   message])
