(ns tests.util.ui
  (:require ["global-jsdom/register"]
            ["cross-fetch/polyfill"]
            ["@testing-library/react" :as rtl]
            ["react-router-dom" :refer (MemoryRouter)]
            ["@apollo/client" :refer (gql)]
            ["@apollo/client/testing" :refer (MockedProvider)]
            [cljs-bean.core :refer (->js)]
            [promesa.core :as p]
            [reagent.core :as r]
            [ui.components.main :refer (main)]
            [ui.components.routes :refer (routes)]))

(defn before []
  (set! (. js/window -matchMedia)
        (fn []
          (->js {:matches false
                 :addListener (fn [])
                 :removeListener (fn [])}))))

(defn after []
  (rtl/cleanup))

(defn with-mounted-component [comp f]
  (let [^js mounted-component (rtl/render (r/as-element comp))
        cleanup (fn []
                  (.unmount mounted-component)
                  (r/flush))]
    (-> (p/do (f mounted-component))
        (.then cleanup)
        (.catch cleanup))))

(defn click [el]
  (.click rtl/fireEvent el)
  (r/flush))

(defn submit [el]
  (.submit rtl/fireEvent el)
  (r/flush))

(defn change [el value]
  (.change rtl/fireEvent el (->js {:target {:value value}}))
  (r/flush))

(defn memory-router [inital-route & children]
  [:> MemoryRouter {:initial-entries [inital-route]}
   (into [:<>] children)])

(defn apollo-mocked-provider [mocks & children]
  [:> MockedProvider {:mocks mocks :add-typename false}
   (into [:<>] children)])

(def functional-compiler (r/create-compiler {:function-components true}))
(r/set-default-compiler! functional-compiler)

(defn test-app [{:keys [route mocks]}]
  [memory-router route
   [apollo-mocked-provider (-> (map #(update-in % [:request :query] gql) mocks) ->js)
    [main
     [routes]]]])

