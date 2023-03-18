(ns ui.components.layout.header
  (:require ["react-feather" :rename {Sidebar SidebarIcon}]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.utils.string :refer (class-names)]
            [ui.components.inputs.back-button :refer (back-button)]))

(defn header [{:keys [title actions]}]
  (let [sidebar-open (listen [:layout/sidebar-open])]
    [:header
     [:div {:class "px-4 h-14 border-b border-neutral-700 flex justify-between items-center"}
      [:div {:class "flex items-center w-full truncate"}
       [:button {:class "z-20 relative lg:hidden mr-4 text-neutral-400 hover:text-neutral-50 focus:text-neutral-50"
                 :on-click #(dispatch [:layout/toggle-nav])}
        [:> SidebarIcon {:class "w-4 h-4"}]]
       [back-button {:class "mr-4"}]
       [:h1 {:class (class-names
                     (when sidebar-open "z-30")
                     "text-sm font-medium"
                     "truncate")}
        title]]
      [:div {:class "flex-shrink-0 flex items-center"}
       actions]]]))
