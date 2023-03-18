(ns ui.components.inputs.dropzone
  (:require ["react-dropzone" :refer (useDropzone)]
            [cljs-bean.core :refer (->clj)]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [ui.utils.string :refer (class-names)]))

(defn ->kebab-case [^js obj]
  (cske/transform-keys csk/->kebab-case-keyword (->clj obj)))

(defn set-ref [^js obj]
  #(set! (.. obj -ref -current) %))

(defn tranform-props [^js props]
  (merge (->kebab-case props) {:ref (set-ref props)}))

(defn dropzone [{:keys [accept on-drop]}]
  (let [{:keys [getRootProps
                getInputProps
                isFocused
                isDragAccept
                isDragReject]} (->clj (useDropzone #js{:accept accept :onDrop on-drop}))
        container-class (class-names
                         "transition"
                         "cursor-pointer"
                         "flex flex-col items-center justify-center w-full h-full"
                         "rounded border-2 border-dashed outline-0"
                         (cond isDragAccept "border-green-500 bg-green-800/50"
                               isDragReject "border-red-500 bg-red-800/50"
                               isFocused "border-blue-500 bg-blue-800/50"
                               :else "border-neutral-500 bg-neutral-800/50"))
        ^js root-props (getRootProps #js{:class container-class})
        ^js input-props (getInputProps #js{:multiple false})]
    [:div (tranform-props root-props)
     [:input (tranform-props input-props)]
     [:div {:class "p-4 select-none"}
      (cond
        isDragAccept "Drop the files here"
        isDragReject "These files will be rejected"
        :else "Drag and drop files here, or click to select files")]]))
