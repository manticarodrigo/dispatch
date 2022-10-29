(ns tests.user
  (:require
   [shadow.resource :refer (inline)]
   [cljs.test :as t :refer-macros [deftest async is]]
   [promesa.core :as p]
   [tests.util.request :refer (send)]))

(deftest register
  (async done
         (p/let [query (inline "mutations/user/register.graphql")
                 payload  {:query query
                           :variables {:firstName "test"
                                       :lastName "test"
                                       :email "test@test.test"
                                       :password "test"}}
                 ^js res (send payload)
                 ^js res2 (send payload)
                 ^js error (first (.. res2 -body -errors))
                 ^js anom (.. error -extensions -anom)]
           (is (some? (.. res -body -data -register)))
           (is (= "conflict" (. anom -category)))
           (is (= "unique-constraint" (. anom -reason)))
           (is (= "email" (first (.. anom -meta -target))))
           (done))))

(deftest login
  (async done
         (p/let [query (inline "mutations/user/login.graphql")
                 ^js res (send
                          {:query query
                           :variables {:email "test@test.test"
                                       :password "test"}})
                 ^js res2 (send
                           {:query query
                            :variables {:email "test@test.test"
                                        :password "incorrect"}})
                 ^js error (first (.. res2 -body -errors))
                 ^js anom (.. error -extensions -anom)
                 ^js res3 (send
                           {:query query
                            :variables {:email "not@found.test"
                                        :password "test"}})
                 ^js error2 (first (.. res3 -body -errors))
                 ^js anom2 (.. error2 -extensions -anom)]
           (is (some? (.. res -body -data -login)))
           (is (= "forbidden" (. anom -category)))
           (is (= "invalid-password" (. anom -reason)))
           (is (= "not-found" (. anom2 -category)))
           (is (= "account-not-found" (. anom2 -reason)))
           (done))))

(deftest delete
  (async done
         (p/let [query (inline "mutations/user/delete.graphql")
                 ^js res (send
                          {:query query
                           :variables {:email "test@test.test"}})]
           (is (some? (.. res -body -data -delete)))
           (done))))
