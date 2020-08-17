(ns nifty.test
  (:require
    [doo.runner :refer-macros [doo-tests]]
    [nifty.disjoint-set-test]
    [nifty.priority-map-test]))

(doo-tests 'nifty.priority-map-test
           'nifty.disjoint-set-test)
