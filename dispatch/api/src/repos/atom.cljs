(ns repos.atom
  (:require [repo]))

(def comments (atom []))

(defn add-comment
  [new-comment]
  (swap!
   comments
   (fn [current-comments]
     (conj current-comments new-comment)))
  new-comment)

(defmethod repo/save-comment :local
  [_ new-comment]
  (add-comment new-comment))

(defn list-comments
  [post-id]
  (reverse (filter #(= (:post-id %) post-id) @comments)))

(defmethod repo/get-comments :local
  [_ post-id]
  (list-comments post-id))
