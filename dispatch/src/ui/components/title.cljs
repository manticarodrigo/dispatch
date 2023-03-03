(ns ui.components.title
  (:require [react-feather :rename {Sidebar SidebarIcon
                                    Plus PlusIcon}]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.lib.router :refer (link)]))

(defn title [{:keys [title create-link]}]
  (let [sidebar-open (listen [:layout/sidebar-open])]
    [:header {:class "px-4 py-4 border-b border-neutral-700 flex justify-between items-center"}
     [:div {:class "flex items-start"}
      [:button {:class "z-20 relative lg:hidden mr-2 text-neutral-400 hover:text-neutral-50 focus:text-neutral-50"
                :on-click #(dispatch [:layout/toggle-nav])}
       [:> SidebarIcon {:class "w-4 h-4"}]]
      [:h1 {:class (class-names
                    "text-sm font-medium leading-4"
                    (when sidebar-open "z-30"))}
       title]]
     [:div {:class "flex items-start"}
      (when create-link
        [link {:to create-link :class "text-sm leading-4 underline capitalize"}
         [:> PlusIcon {:class "inline mr-1 w-3 h-3"}] (tr [:verb/create])])
      [:button {:class "z-30 relative xl:hidden ml-2 text-neutral-400 hover:text-neutral-50 focus:text-neutral-50"
                :on-click #(dispatch [:layout/toggle-sidebar])}
       [:> SidebarIcon {:class "rotate-180 w-4 h-4"}]]]]))
