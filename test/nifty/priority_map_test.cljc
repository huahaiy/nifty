(ns nifty.priority-map-test
  (:require
   [nifty.priority-map :as sut]
   #?(:clj [clojure.test :refer [are deftest]]
      :cljs [cljs.test :refer [are deftest] :include-macros true])))

(deftest test-priority-map
  (let [a (sut/priority-map :e 5 :b 2 :c 3 :d 4 :a 1 :f 6)]
    (are [x y] (= x y)
      (empty? a)              false
      (peek a)                [:a 1]
      (peek (pop a))          [:b 2]
      (peek (assoc a :z 0))   [:z 0]
      (peek (conj a [:a -1])) [:a -1])))
