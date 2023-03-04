(ns tests.util.ui
  (:require ["global-jsdom/register"]
            ["cross-fetch/polyfill"]
            ["@testing-library/react" :as rtl]
            ["@testing-library/user-event$default" :as user-event]
            ["react-router-dom" :refer (MemoryRouter)]
            ["@apollo/client" :refer (gql)]
            ["@apollo/client/testing" :refer (MockedProvider)]
            ["@faker-js/faker" :refer (faker)]
            [cljs-bean.core :refer (->js)]
            [promesa.core :as p]
            [reagent.core :as r]
            [ui.events]
            [ui.components.routes :refer (routes)]))

(rtl/configure #js{:asyncUtilTimeout 5000})

(defn before []
  (.setLocale faker "es")
  (set! (. js/window -matchMedia)
        (fn []
          (->js {:matches false
                 :addListener (fn [])
                 :removeListener (fn [])})))
  (set! js/IntersectionObserver
        (fn []
          (->js {:observe (fn [])
                 :unobserve (fn [])
                 :disconnect (fn [])}))))

(defn after []
  (rtl/cleanup))

(defn with-mounted-component [comp f]
  (let [^js mounted-component (rtl/render (r/as-element comp))
        user (.setup user-event)
        cleanup (fn []
                  (.unmount mounted-component)
                  (r/flush))]
    (-> (p/do (f mounted-component user))
        (.then cleanup))))

(defn click [el]
  (.click rtl/fireEvent el)
  (r/flush))

(defn submit [el]
  (.submit rtl/fireEvent el)
  (r/flush))

(defn change [el value]
  (.change rtl/fireEvent el (->js {:target {:value value}}))
  (r/flush))

(defn select [^js user el value]
  (p/do
    (.selectOptions user el value)
    (r/flush)))

(defn get-combobox [^js component label]
  (.getByLabelText component label #js{:selector "input"}))

(defn select-combobox [user ^js component label value]
  (p/do
    (click (get-combobox component label))
    (select user (.getByRole component "listbox")
            value)))

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
    [routes]]])
