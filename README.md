# nifty

A collection of useful data structures for Clojure and Clojurescript.

These data structures are intended to be used in program hotspots, so the design favors performance over immutable data semantics. That is to say, these data structures are mutalbe, though familar Clojure collection protocols are implemented.

##### Table of Contents

[Priority Map](#priority-map)
[Disjoint Set (Union-find)](#disjoint-set)

<a name="priority-map"/>
## Priority Map

This is a [priority queue](https://en.wikipedia.org/wiki/Priority_queue) that
also functions as a map.

The keys are the data, the values are the priorities. Data are kept in sorted order based on the priority.

Priority queue is often used in breadth first search based graph algorithms, discrete event simulation, resource management and scheduling.

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

We do not implement the optional `decrease-key` operation, as most uses of priority queue care only about the head of the queue, and the map avoids duplicates already.

Priority map does not support `dissoc` on an arbitrary key.

<a name="disjoint-set"/>
## Disjoint Set

This is a
[disjoint-set](https://en.wikipedia.org/wiki/Disjoint-set_data_structure), also
known as a union-find data structure. It consists of some disjoint sets of
values, where each set has a canonical value representing the set.

Disjoint-set is often used to keep track of connected components of a graph, and
to implement unification.

In addition to `canonical` (find) and `union` function, normal set operations
are defined on the data structure, as well as some convenient functions: `sets`
return a map of canonical values to the corresponding sets of values; `members`
return the set a value belongs to.

```Clojure
```

We implements path compression and union by rank optimizations. The amortized
time complexity is O(m`a`(n)) for m union-find operations on n elements, which is optimal, where `a` is inverse Ackermann function.
