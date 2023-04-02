(ns ui.components.browser)

(defn browser [& children]
  [:div {:class "relative row-start-1 col-start-6 xl:col-start-7 col-span-7 xl:col-span-6"}
   [:div {:class "-mx-4 sm:mx-0"}
    [:div {:class "relative border border-neutral-700 rounded-xl bg-neutral-900/10 backdrop-blur shadow-xl"}
     [:div {:class "relative w-full flex flex-col"}
      [:div {:class "flex-none border-b border-neutral-700"}
       [:div {:class "flex items-center h-8 space-x-1.5 px-3"}
        [:div {:class "w-2.5 h-2.5 bg-neutral-600 rounded-full"}]
        [:div {:class "w-2.5 h-2.5 bg-neutral-600 rounded-full"}]
        [:div {:class "w-2.5 h-2.5 bg-neutral-600 rounded-full"}]]]
      [:div {:class "relative min-h-0 flex-auto flex flex-col"}
       [:div {:class "w-full flex-auto flex min-h-0 max-h-[70vh]"}
        [into [:<>] children]]]]]]])
