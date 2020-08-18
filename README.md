# nifty

[![Clojars Project](https://img.shields.io/clojars/v/huahaiy/nifty.svg?color=sucess)](https://clojars.org/huahaiy/nifty)

A collection of useful data structures for Clojure and Clojurescript.

These data structures are intended to be used in program hotspots, so the design favors performance over immutable data semantics. That is to say, these data structures are mutalbe, though familar Clojure collection protocols are implemented.

##### Table of Contents

* [Priority Map](#priority-map)
* [Disjoint Set (Union-find)](#disjoint-set)

## Priority Map
<a name="priority-map"/>

This is a [priority queue](https://en.wikipedia.org/wiki/Priority_queue) that
also functions as a map.

The keys are the data, the values are the priorities. Data are kept in sorted order based on the priority.

Priority queue is often used in breadth first search based graph algorithms, discrete event simulation, resource management and scheduling.

```Clojure
(use 'nifty.priority-map')

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

We do not implement the optional `decrease-key` operation, as most uses of priority queue care only about the head of the queue, and the map avoids duplicates already.

Priority map does not support `dissoc` on an arbitrary key.

## Disjoint Set
<a name="disjoint-set"/>

This is a
[disjoint-set](https://en.wikipedia.org/wiki/Disjoint-set_data_structure), also
known as a union-find data structure. It consists of some disjoint sets of
values, where each set has a canonical value representing it.

Disjoint-set is often used to keep track of connected components of a graph, and
to implement unification.

In addition to `canonical` (find) and `union` function, set operations
are defined on the data structure, as well as some convenient functions: `sets`
return a map of canonical values to the corresponding sets of values; `members`
return the set a value belongs to.

```Clojure
(use 'nifty.disjoint-set)

;; define a disjoint set
(def a (disjoiont-set 1 2))

;; At this point, each value is its own canonical value
a
;;=> #{1 2}

(canonical a 1)
;;=> 1
(canonical a 2)
;;=> 2

;; Now union 1 and 2
(union a 1 2)
;;=> #{2}

a
;;=> #{2}

(canonical a 1)
;;=> 2
(canonical a 2)
;;=> 2

;; Now add a value
(conj a 3)
;;=> #{2 3}

;; see all the sets so far
(sets a)
;;=> {2 [1 2], 3 [3]}
(members a 1)
;;=> #{1 2}

;; Now remove 2
(disj a 2)
;;=> #{1 3}

;; now 1 becomes the canonical value
(sets a)
;;=> {1 [1], 3 [3]}
(members a 1)
;;=> #{1}
```

We implements path compression and union by rank optimizations. The amortized
time complexity is O(m`a`(n)) for m union-find operations on n elements, which is optimal, where `a` is inverse Ackermann function.
