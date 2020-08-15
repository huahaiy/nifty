# nifty

Some useful data structures for Clojure and Clojurescript.

The intent of these data structures is to be used in the program hotspots, so the design favors performance over immutable data semantics. That is to say, these data structures are mutalbe, though familar Clojure collection protocols are implemented.

## Priority Map

This is a priority queue that also functions as a map. The keys are the data,
the values are the priorities. Data are kept in sorted order based on the priority.


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

;; insert a key priority pair
(assoc m :z 0)
;;=> {:e 5, :b 2, :c 3, :d 4, :f 6, :z 0}

;; peek
(peek m)
;;=> [:z 0]

```

The priority queue is implemented as a [pairing heap](https://en.wikipedia.org/wiki/Pairing_heap). The time complexity of `peek` and `assoc` are both O(1), and `pop` is O(log n).

We do not implement the optional `decrease-key` operation, since most use of priority queue care only about the head of the queue, and the map avoids duplicates already.

