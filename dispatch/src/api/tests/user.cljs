(ns api.tests.user
  (:require
   [shadow.resource :refer (inline)]
   [cljs.test :as t :refer-macros [deftest async is]]
   [promesa.core :as p]
   [api.util.test :refer (send)]))

(deftest register
  (async done
         (p/let [query (inline "mutations/user/register.graphql")
                 ^js res (send {:query query
                            :variables {:firstName "test"
                                        :lastName "test"
                                        :email "test@test.test"
                                        :password "test"}})]
           (is (some? (.. res -body -data -register)))
           (done))))

(deftest login
  (async done
         (p/let [query (inline "mutations/user/login.graphql")
                 ^js res (send {:query query
                            :variables {:email "test@test.test"
                                        :password "test"}})]
           (is (some? (.. res -body -data -login)))
           (done))))

(deftest delete
  (async done
         (p/let [query (inline "mutations/user/delete.graphql")
                 ^js res (send {:query query
                            :variables {:email "test@test.test"}})]
           (is (some? (.. res -body -data -delete)))
           (done))))
