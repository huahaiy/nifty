(ns nifty.priority-map
  #?(:clj
     (:import [clojure.lang IPersistentStack IPersistentMap]
              [java.io Writer])))

(defprotocol ^:no-doc IHeapNode
  (get-left [this] "Get the left child node")
  (get-right [this] "Get the right sibling node")
  (set-right [this right] "Set the right sibling")
  (add-child [this node] "Add a child to a node"))

(deftype ^:no-doc HeapNode [item
                            ^long priority
                            ^:unsynchronized-mutable left
                            ^:unsynchronized-mutable right]
  IHeapNode
  (get-left [_] left)
  (get-right [_] right)
  (set-right [_ r] (set! right r))
  (add-child [this node]
    (when left (set-right node left))
    (set! left node)
    this))

#?(:clj (defmethod print-method HeapNode
          [x ^Writer writer]
          (print-method {:item     (.-item ^HeapNode x)
                         :priority (.-priority ^HeapNode x)
                         :left     (get-left x)
                         :right    (get-right x)}
                        writer)))

(defn- merge-nodes
  [^HeapNode a ^HeapNode b]
  (cond
    (nil? a)                          b
    (nil? b)                          a
    (< (.-priority a) (.-priority b)) (add-child a b)
    :else                             (add-child b a)))

(defn- insert
  [^HeapNode node item priority]
  (merge-nodes node (->HeapNode item priority nil nil)))

(defn- two-pass
  [^HeapNode node]
  (if (or (nil? node) (nil? (get-right node)))
    node
    (let [a node
          b (get-right node)
          n (get-right b)]
      (set-right a nil)
      (set-right b nil)
      (merge-nodes (merge-nodes a b) (two-pass n)))))

#?(:clj
   (deftype ^:no-doc PriorityMap [^:unsynchronized-mutable ^HeapNode heap
                         ^:unsynchronized-mutable map]
     IPersistentMap
     (seq [_] (seq map))
     (count [_] (count map))
     (cons [this e]
       (let [[item priority] e]
         (set! map (assoc map item priority))
         (set! heap (insert heap item priority))
         this))
     (empty [this]
       (set! heap nil)
       (set! map {})
       this)
     (equiv [this o] (identical? this o))
     (assoc [this item priority]
       (set! map (assoc map item priority))
       (set! heap (insert heap item priority))
       this)
     (hashCode [_] (hash map))
     (equals [this o] (identical? this o))
     (containsKey [_ item] (contains? map item))
     (entryAt [_ k] (find map k))
     (without [this item] (throw (ex-info "Not implemented" {})))

     IPersistentStack
     (peek [_] [(.-item heap) (.-priority heap)])
     (pop [this]
       (let [n (two-pass (get-left heap))]
         (set! map (dissoc map (.-item heap)))
         (set! heap n)
         this)))

   :cljs
   (deftype ^:no-doc PriorityMap [^:mutable ^HeapNode heap
                         ^:mutable map]

     ISeqable
     (-seq [_] (seq map))

     ICounted
     (-count [this] (count (seq this)))

     ICollection
     (-conj [this e]
       (let [[item priority] e]
         (set! map (assoc map item priority))
         (set! heap (insert heap item priority))
         this))

     IAssociative
     (-assoc [this item priority]
       (set! map (assoc map item priority))
       (set! heap (insert heap item priority))
       this)
     (-contains-key? [_ item] (contains? map item))

     IMap
     (-dissoc [this item] (throw (ex-info "Not implemented" {})))

     IStack
     (-peek [_] [(.-item heap) (.-priority heap)])
     (-pop [this]
       (let [n (two-pass (get-left heap))]
         (set! map (dissoc map (.-item heap)))
         (set! heap n)
         this))))

(defn priority-map
  "Create a priority map, which is a priority queue that also functions as a
  map."
  ([]
   (->PriorityMap nil {}))
  ([& keyvals]
   {:pre [(even? (count keyvals))]}
   (reduce conj (priority-map) (partition 2 keyvals))))
