(ns ui.components.layout.header
  (:require [react-feather :rename {Sidebar SidebarIcon
                                    Edit CreateIcon}]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.router :refer (link)]
            [ui.components.inputs.back-button :refer (back-button)]
            [ui.components.inputs.button :refer (button-class)]))

(defn header [{:keys [title create-link]}]
  (let [nav-open (listen [:layout/nav-open])
        sidebar-open (listen [:layout/sidebar-open])]
    [:header
     [:div {:class "px-4 h-14 border-b border-neutral-700 flex justify-between items-center"}
      [:div {:class "flex items-center w-full truncate"}
       [:button {:class "z-20 relative lg:hidden mr-2 text-neutral-400 hover:text-neutral-50 focus:text-neutral-50"
                 :on-click #(dispatch [:layout/toggle-nav])}
        [:> SidebarIcon {:class "w-4 h-4"}]]
       [back-button {:class "ml-2 mr-4"}]
       [:h1 {:class (class-names
                     (when sidebar-open "z-30")
                     "text-sm font-medium"
                     "truncate")}
        title]]
      [:div {:class "flex-shrink-0 flex items-center"}
       (when create-link
         [link {:to create-link :class (class-names button-class "capitalize flex items-center")}
          [:> CreateIcon {:class "inline mr-1 w-4 h-4"}] (tr [:verb/create])])
       [:button {:class (class-names
                         (when-not nav-open "z-30")
                         "relative xl:hidden"
                         "ml-2"
                         "text-neutral-400 hover:text-neutral-50 focus:text-neutral-50")
                 :on-click #(dispatch [:layout/toggle-sidebar])}
        [:> SidebarIcon {:class "rotate-180 w-4 h-4"}]]]]]))
