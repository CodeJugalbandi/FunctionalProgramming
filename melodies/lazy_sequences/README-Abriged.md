# Lazy Sequences

### eager lists
 
**BRAMHA** In Clojure:

```
(range 1 10)
;; (1 2 3 4 5 6 7 8 9)
```
**BRAMHA** Let's find our way towards a recursive definition for range:

```
(range 1 3)
(cons 1 (range 2 3))
(cons 1 (cons 2 (range 3 3)))
(cons 1 (cons 2 []))
;; '(1 2)
```
**BRAMHA** i.e.

```
(defn -range [start end]
  (if (= start end)
    []
    (cons start
          (-range (+ 1 start) end))))
```

**BRAMHA** When (range 1 3) evaluates, recursively, as '(1 2), the whole 
list is "realised" immediately.

### lazy lists

**BRAMHA** In contrast to this, in Functional programming, we also have **lazy lists**:

```
(range 1 3)
(cons 1 (fn [] (range 2 3)))
```
**BRAMHA** The head of the list is realised, but the tail is not.

**BRAMHA** cons'ing something onto a lambda like this won't work in Clojure like 
this, so I'm going to write a lazy cons:

```
(defn lazy-cons [_car _cdr]
  (fn [msg]
    (cond (= msg 'head)
          _car
          (= msg 'tail)
          (_cdr))))
```
**BRAMHA** To get at the tail's contents, we have to evaluate the "tail function".
It's only when we select the 'tail' of the list that it get's "realised".

**BRAMHA** Let's build up a lazy list with our new cons:

```
(def a-lazy-list (lazy-cons 1
                            (fn [] (lazy-cons 2
                                             (fn [] (lazy-cons 3
                                                              (fn [] '())))))))
```

### walking a lazy list

**BRAMHA**  For a lazy list, the tail is only evaluated when you select it.
For regular, "eager" lists, the head and tail are both evaluated at contruction.

**BRAMHA**  As you walk the list, the tail is only 'realised' when you select it,
and even then, that tail is only a realised head with a delayed tail!
Each element is realised only when it is reached by list traversal.

```
(println (a-lazy-list 'head))
(println ((a-lazy-list 'tail) 'head))
(println ((a-lazy-list 'tail) 'tail))
(println (((a-lazy-list 'tail) 'tail) 'head))
```

**BRAMHA** A lazy list is a very dynamic thing, it is called into being as you 
traverse it. As if the ground materialises under you as you walk!

**BRAMHA** In Clojure, the regular map HOF is indeed lazy, as are many of the 
sequence ops. Same thing in Haskell: the default behaviour for lists is lazy
(in Haskell, this is true for **all** evaluation!)

### Lazy List in Scala
**KRISHNA** In Scala, a list whose tail is evaluated only on a pull, that is, lazily, is a Stream.  It is a recursive structure like list.  Here is how you construct one.

```
val s1 = Stream(1, 2, 3) 
```

**KRISHNA** The operational behavior of Stream is different from that of a list, like you said, the head is realized, whereas the tail is only evaluated on pull.  The '?' mark stands for unevaluated stream.  So these are all partially constructed streams until materialized.

```
s1.head //1
s1.tail //Stream(2, ?)
```

**KRISHNA** You can also force the entire stream to materialize by calling a `toList` on it.

```
s1.toList
```

### Lazy sequencing

**BRAMHA** A major benefit of lazyness is that lazy lists allows us to do 
sequence operations without the cost of lists, e.g., earlier, when we explored 
sequencing, we looked at pipelines of functions like this:

```
(->> (range 1 1000)
     (map (partial * 2))
     (filter (partial > 10)))

;; (2 4 6 8)
```              
**BRAMHA** Let's go into how this pipeline might be  evaluated...
**BRAMHA** If we take an "eager" approach to evaluation, it would look like this:

```
(range 1 1000) -> (1 2 3 ... 1000)
(map (* 2))    -> (2 4 6 8 ... 2000)
(filter ...)   -> (2 4 6 8)
```
**BRAMHA** ... all of each list must be held in memory at a time.

**BRAMHA**  In the case of Lazy sequences:

```
(range 1 1000)
;;  (lazy-cons 1 (fn [] (range 2 1000)))

(map f (range 1 1000))
;; lazy-cons (* 2 1) (fn [] (map f (range 2 1000))) 
```

**BRAMHA** In this way, with lazy lists and lazy operators, the list items flow
 down the pipeline one at a time.

**BRAMHA** In Clojure, the above pipeline expression is indeed evaluated in 
this Lazy way: range returns a "Lazy seq", map and filter operate on and 
return lazy seqs.

**KRISHNA** You can transform the elements of a Stream using `map`, but the transformation won't happen until evaluated.

```
val s4 = s1.map(_ * 2)
s4.take(2).toList  //List(1, 4)
```

**KRISHNA** The virtue in being lazy is that - we not only allocate the space required, but also can delay the computation until the demand is placed. Another name for lazy or pull-based collections is "non-strict" collections as opposed to "strict" or eager collections. 

### Lazy sequencing In Scala
**KRISHNA** Let's now see lazy sequencing in Scala.

```
val l = List(1 to 10: _*).map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 5
    }
``` 
**KRISHNA** Now with Streams...

```
val s = Stream(1 to 10:  _*).map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 5
    }

s.take(2).toList
```
**KRISHNA** So we saw how items flow one at a time in the pipeline.

**KRISHNA** In Scala, you can convert a "eager" collection to a "lazy" collection by using views.
```
val l = List(1 to 10:  _*).view.map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 5
    }
    
l.take(2).toList
``` 

**KRISHNA** This is lazy sequences, but lazy evaluation also applies to function arguments and 
in Scala and in Clojure, it is strict evaluation.  However, Haskell uses lazy evaluation
for all its function args by default.