# nifty

Some useful data structures.

## Priority Map

This is a priority queue that also functions as a map. The keys are the data,
the values are the priorities. Data are kept in sorted order by priority.

```Clojure
;; initialize a priority map
(def m (priority-map :e 5 :b 2 :c 3 :d 4 :a 1 :f 6))

;; peek the head
(peek m)
;;=> [:a 1]

;; pop the head
(pop m)
;;=> {:e 5, :b 2, :c 3, :d 4, :f 6}

;; peek again
(peek m)
;;=> [:b 2]

;; insert a key value pair
(assoc m :z 0)
;;=> {:e 5, :b 2, :c 3, :d 4, :f 6, :z 0}

;; peek
(peek m)
;;=> [:z 0]

```
