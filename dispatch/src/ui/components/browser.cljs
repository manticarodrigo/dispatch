(ns ui.components.browser)

(defn browser [& children]
  [:div {:class "relative row-start-1 col-start-6 xl:col-start-7 col-span-7 xl:col-span-6"}
   [:div {:class "-mx-4 sm:mx-0"}
    [:div {:class "relative border border-neutral-600/50 rounded-xl bg-neutral-900/25 backdrop-blur bg-clip-padding shadow-xl"}
     [:div {:class "relative w-full flex flex-col"}
      [:div {:class "flex-none rounded-t-xl border-b border-neutral-600/50 bg-neutral-700/10"}
       [:div {:class "flex items-center h-8 space-x-1.5 px-3"}
        [:div {:class "w-2.5 h-2.5 bg-neutral-500 rounded-full"}]
        [:div {:class "w-2.5 h-2.5 bg-neutral-500 rounded-full"}]
        [:div {:class "w-2.5 h-2.5 bg-neutral-500 rounded-full"}]]]
      [:div {:class "relative min-h-0 flex-auto flex flex-col"}
       [into [:<>] children]]]]]])
