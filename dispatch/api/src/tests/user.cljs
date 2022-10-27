(ns tests.user
  (:require [cljs.test :as t :refer-macros [deftest async is]]
            [promesa.core :as p]
            [util.resource :refer (slurp)]
            [util.test :refer (send)]))

(deftest register
  (async done
         (p/let [query (slurp "mutations/user/register.graphql")
                 res (send {:query query
                            :variables {:firstName "test"
                                        :lastName "test"
                                        :email "test@test.test"
                                        :password "test"}})]
           (is (some? (.. res -body -data -register)))
           (done))))

(deftest login
  (async done
         (p/let [query (slurp "mutations/user/login.graphql")
                 res (send {:query query
                            :variables {:email "test@test.test"
                                        :password "test"}})]
           (is (some? (.. res -body -data -login)))
           (done))))

(deftest delete
  (async done
         (p/let [query (slurp "mutations/user/delete.graphql")
                 res (send {:query query
                            :variables {:email "test@test.test"}})]
           (is (some? (.. res -body -data -delete)))
           (done))))
