(ns nifty.disjoint-set-test
  (:require [nifty.disjoint-set :as sut]
            #?(:clj [clojure.test :refer [is deftest]]
               :cljs [cljs.test :refer [is deftest] :include-macros true])))

(deftest test-union-find
  (let [a (sut/disjoint-set 1 2)]
    (is (= (set (seq a)) #{1 2}))
    (is (= (str a) "{1 [1], 2 [2]}"))
    (is (= 2 (count a)))
    (is (contains? a 1))
    (is (sut/sets a) {1 [1], 2 [2]})
    (is (= (sut/members a 1) #{1}))
    (is (= 1 (sut/canonical a 1)))
    (is (= 2 (sut/canonical a 2)))

    (sut/union a 1 2)
    (is (= 1 (count a)))
    (is (contains? a 1))
    (is (contains? a 2))
    (is (= (sut/members a 1) #{1 2}))
    (is (= (sut/canonical a 2) (sut/canonical a 1)))

    (conj a 3)
    (is (= 2 (count a)))
    (is (contains? a 1))
    (is (contains? a 2))
    (is (contains? a 3))
    (is (= (sut/members a 1) #{1 2}))
    (is (= (sut/members a 3) #{3}))
    (is (= (sut/canonical a 2) (sut/canonical a 1)))
    (is (not= (sut/canonical a 2) (sut/canonical a 3)))

    (disj a 2)
    (is (= 2 (count a)))
    (is (contains? a 1))
    (is (not (contains? a 2)))
    (is (contains? a 3))
    (is (= 1 (sut/canonical a 1)))
    (is (= (sut/members a 1) #{1}))
    (is (= (sut/members a 3) #{3}))))
