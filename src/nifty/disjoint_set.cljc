(ns nifty.disjoint-set
  #?(:clj
     (:import [clojure.lang IPersistentSet]
              [java.io Writer])))

(defprotocol ^:no-doc IElement
  (value [this])
  (rank [this])
  (parent [this])
  (set-rank [this rank])
  (set-parent [this parent]))

(deftype ^:no-doc Element [v
                           ^:unsynchronized-mutable r
                           ^:unsynchronized-mutable p]
  Object
  (equals [this o] (identical? this o))

  IElement
  (value [_] v)
  (rank [_] r)
  (parent [_] p)
  (set-rank [this rank]
    (set! r rank)
    this)
  (set-parent [this parent]
    (set! p parent)
    this))

#?(:clj (defmethod print-method Element
          [^Element x ^Writer writer]
          (print-method {:value  (.-v  x)
                         :parent (.-v ^Element (parent x))
                         :rank   (rank x)}
                        writer)))

(defn- insert
  [elements v]
  (let [element (->Element v 0 nil)]
    (set-parent element element)
    (assoc elements v element)))

(defn- canonical?
  [^Element element]
  (= element (parent element)))

(defprotocol IDisjointSet
  (canonical [this v]
    "Find the canonical value of the given value of the universe, i.e.
    the `find` in union-find.")
  (union [this x y]
    "Union two values in the universe so that they share the same canonical
     value, i.e. the `union` in union-find. Do nothing if one of the values
     does not belong to the universe.")
  (sets [this]
    "Return a map of canonical values to the corresponding sets of values")
  (members [this v]
    "Return the members of the set that shares the same canonical value as the
     given value"))

#?(:clj
   (deftype DisjointSet [^:unsynchronized-mutable elements]
     IPersistentSet
     (seq [_]
       (map key (filter (comp canonical? val) elements)))
     (count [this] (count (seq this)))
     (cons [this v]
       (when-not (elements v)
         (set! elements (insert elements v)))
       this)
     (empty [this]
       (set! elements {})
       this)
     (equiv [this o]
       (identical? this o))
     (disjoin [this v]
       (when-let [element (elements v)]
         (when (canonical? element)
           (when-let [group ((sets this) v)]
             (when-let [new (->> group
                                 (filter #(not= element (elements %)))
                                 (sort-by #(rank (elements %)) >)
                                 first
                                 elements)]
               (set-parent new new)
               (doseq [x group]
                 (set-parent (elements x) new)))))
         (set! elements (dissoc elements v))
         this))
     (contains [this v]
       (some? (canonical this v)))
     (get [this v]
       (canonical this v))
     (toString [this] (str (group-by (partial canonical this) (keys elements))))
     (hashCode [_] (hash elements))
     (equals [this o] (identical? this o))

     IDisjointSet
     (canonical [this v]
       (when-let [e (elements v)]
         (let [root (loop [r e]
                      (let [p (parent r)]
                        (if (= p r)
                          r
                          (recur p))))]
           (loop [n e]
             (let [p (parent n)]
               (if (= p root)
                 (.-v ^Element root)
                 (do (set-parent n root)
                     (recur p))))))))
     (union [this x y]
       (let [xc           (canonical this x)
             yc           (canonical this y)
             rx           (elements xc)
             ry           (elements yc)
             ^long rank-x (rank rx)
             ^long rank-y (rank ry)]
         (cond
           (or (nil? xc) (nil? yc) (= xc yc)) :no-op
           (= rank-x rank-y)                  (do (set-parent rx ry)
                                                  (set-rank ry (inc rank-y)))
           (< rank-x rank-y)                  (set-parent rx ry)
           (> rank-x rank-y)                  (set-parent ry rx))
         this))
     (sets [this]
       (group-by (partial canonical this) (keys elements)))
     (members [this v]
       (set ((sets this) (canonical this v)))))

   :cljs
   (deftype DisjointSet [^:mutable elements]
     Object
     (equiv [this o]
       (identical? this o))
     (toString [this]
       (str (group-by (partial canonical this) (keys elements))))

     ISeqable
     (-seq [_]
       (map key (filter (comp canonical? val) elements)))

     ICollection
     (-conj [this v]
       (when-not (elements v)
         (set! elements (insert elements v)))
       this)

     ICounted
     (-count [this] (count (seq this)))

     IEmptyableCollection
     (-empty [this]
       (set! elements {})
       this)

     ISet
     (-disjoin [this v]
       (when-let [element (elements v)]
         (when (canonical? element)
           (when-let [group ((sets this) v)]
             (when-let [new (->> group
                                 (filter #(not= element (elements %)))
                                 (sort-by #(rank (elements %)) >)
                                 first
                                 elements)]
               (set-parent new new)
               (doseq [x group]
                 (set-parent (elements x) new)))))
         (set! elements (dissoc elements v))
         this))

     ILookup
     (-lookup [this v]
       (-lookup this v nil))
     (-lookup [this v not-found]
       (or (canonical this v)
           not-found))

     IHash
     (-hash [_] (hash elements))

     IDisjointSet
     (canonical [this v]
       (when-let [e (elements v)]
         (let [root (loop [r e]
                      (let [p (parent r)]
                        (if (= p r)
                          r
                          (recur p))))]
           (loop [n e]
             (let [p (parent n)]
               (if (= p root)
                 (.-v ^Element root)
                 (do (set-parent n root)
                     (recur p))))))))
     (union [this x y]
       (let [xc           (canonical this x)
             yc           (canonical this y)
             rx           (elements xc)
             ry           (elements yc)
             ^long rank-x (rank rx)
             ^long rank-y (rank ry)]
         (cond
           (or (nil? xc) (nil? yc) (= xc yc)) :no-op
           (= rank-x rank-y)                  (do (set-parent rx ry)
                                                  (set-rank ry (inc rank-y)))
           (< rank-x rank-y)                  (set-parent rx ry)
           (> rank-x rank-y)                  (set-parent ry rx))
         this))
     (sets [this]
       (group-by (partial canonical this) (keys elements)))
     (members [this v]
       (set ((sets this) (canonical this v))))))

(defn disjoint-set
  "Create a disjoint-set, a.k.a. union-find, data structure"
  ([]
   (->DisjointSet {}))
  ([& xs]
   (reduce conj (disjoint-set) xs)))
