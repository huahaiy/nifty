(ns nifty.test
  (:require  [doo.runner :refer-macros [doo-tests]]
             [nifty.priority-map-test]))

(doo-tests 'nifty.priority-map-test)
