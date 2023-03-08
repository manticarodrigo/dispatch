(ns ui.components.layout.map
  (:require [react-feather :rename {Sidebar SidebarIcon
                                    Edit CreateIcon}]
            [re-frame.core :refer (dispatch)]
            [ui.subs :refer (listen)]
            [ui.lib.router :refer (link)]
            [ui.utils.i18n :refer (tr)]
            [ui.utils.string :refer (class-names)]
            [ui.components.layout.sidebar :refer (sidebar)]
            [ui.components.layout.header :refer (header)]
            [ui.components.inputs.button :refer (button-class)]
            [ui.components.map :refer (gmap)]))

(defn map-layout [{:keys [title create-link]} & children]
  (let [nav-open (listen [:layout/nav-open])]
    [:div {:class "flex w-full h-full"}
     [:main {:class "flex-shrink-0 flex flex-col w-full xl:w-[450px] h-full"}
      [header {:title title
               :actions [:<>
                         (when create-link
                           [link {:to create-link :class (class-names button-class "capitalize flex items-center")}
                            [:> CreateIcon {:class "inline mr-1 w-4 h-4"}] (tr [:verb/create])])
                         [:button {:class (class-names
                                           (when-not nav-open "z-30")
                                           "relative xl:hidden"
                                           "ml-2"
                                           "text-neutral-400 hover:text-neutral-50 focus:text-neutral-50")
                                   :on-click #(dispatch [:layout/toggle-sidebar])}
                          [:> SidebarIcon {:class "rotate-180 w-4 h-4"}]]]}]
      (into [:<>] children)]
     [sidebar
      [gmap]]]))
