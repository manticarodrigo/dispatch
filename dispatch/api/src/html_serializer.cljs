(ns html-serializer)

(defn zero-pad
  [x len]
  (if (< (count x) len)
    (recur (str "0" x) len)
    x))

(defn format-iso-date
  "Take an ISO-8601 date string and returns a string in the format: '14:45 August 20, 2018'"
  [iso-date]
  (let [date-obj (js/Date. iso-date)
        months ["January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October", "November", "December"]
        date-month (get months (.getMonth date-obj))
        date-year (+ 1900 (.getYear date-obj))]
    (str
     (.getHours date-obj)
     ":"
     (zero-pad (str (.getMinutes date-obj)) 2)
     ", "
     date-month
     " "
     (.getDate date-obj)
     ", "
     date-year)))

(defn any
  [preds val]
  (let [pred-fn (apply juxt preds)
        pred-results (pred-fn val)]
    (reduce #(or %1 %2) pred-results)))

(defn serialize-comment
  [comment-body]
  (let [author (if (any [nil? empty?] (:author comment-body))
                 "Anonymous"
                 (:author comment-body))
        author-section [:p {:class "name"} [:strong author] "said..."]
        message-section [:p {:class "message"} (:message comment-body)]
        date-section [:p {:class "datetime"} (format-iso-date (:time comment-body))]]
    [:div {:class "comment"} author-section message-section date-section]))

(defn serialize-comment-list
  [comments]
  (map serialize-comment comments))
