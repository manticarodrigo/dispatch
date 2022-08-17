(ns app.views.route.nav.summary
  (:require
   [reagent.core :as r]
   [react-feather
    :refer (GitPullRequest Clock)
    :rename {GitPullRequest DistanceIcon Clock DurationIcon}]
   [app.subs :refer (listen)]
   [app.utils.i18n :refer (tr)]
   [app.utils.string :refer (class-names)]
   [app.views.route.utils :refer (distance-str
                                  duration-str
                                  padding)]))

(def !summary-open (r/atom false))

(defn- summary-detail [label value icon]
  [:div {:class "flex flex-col"}
   [:span {:class "flex text-sm text-neutral-300 leading-4"} [:> icon {:size 15 :class "mr-1"}]  label]
   [:span {:class "flex text-lg leading-5"} value]])

(defn summary [class]
  (let [origin (listen [:origin])
        kms (listen [:route/kilometers])
        mins (listen [:route/minutes])]
    (when (some? origin)
      [:div {:class (class-names class padding "grid grid-cols-2 gap-4")}
       [:h2 {:class "col-span-2 flex font-medium text-l"} (tr [:route-view.panel-header/title])]
       [summary-detail (distance-str) (str kms " km") DistanceIcon]
       [summary-detail (duration-str) (str mins " mins") DurationIcon]])))
