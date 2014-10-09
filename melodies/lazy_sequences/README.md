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

### lazy range

**BRAMHA** To come back to our -range function, let's write one that produces a 
lazy list instead of one that materialises all at once:

```
(defn lazy-range [start end]
  (if (= start end)
    (fn [] '()) ; delayed nil list
    (lazy-cons start
               (fn [] ; delayed tail
                 (lazy-range (+ 1 start) end)))))

(def lazy-l (lazy-range 1 3))
(println (lazy-l 'head))
(println ((lazy-l 'tail) 'head))
```

### Lazy Map

**BRAMHA**  Let's say we mapped over this lazy list:

```
(map (partial * 2) (lazy-range 1 3))
```

**BRAMHA**  How might this evaluate? Will our regular list-HOFs still work with 
lazy lists? Let's look at MAP. If we used an eager map function over this lazy 
list, things would get weird:

```
(map f lazy-lst)
(map f (cons 1 (fn [] ?)))
(cons (f 1) (map f (fn [] ?)))
```
**BRAMHA** but (f (fn [] ?)) is non-sensical in this case, because f is a 
function that acts on elements, not strange lambdas!

**BRAMHA** We'll need a specialised map to iterate over these lazy lists:

```
(defn lazy-map [f lst]
  (if (= '() lst)
    (fn [] '())
    (lazy-cons (f (lst 'head))
               (fn [] (lazy-map f (lst 'tail))))))

(def lazy-l (lazy-map (partial * 2)
                      (lazy-range 1 3)))

(lazy-l 'head) ; realise 1*2
((lazy-l 'tail) 'head) ; realise 2*2
```

**BRAMHA** A lazy list is a very dynamic thing, it is called into being as you 
traverse it. As if the ground materialises under you as you walk!

**BRAMHA** In Clojure, the regular map HOF is indeed lazy, as are many of the 
sequence ops. Same thing in Haskell: the default behaviour for lists is lazy
(in Haskell, this is true for **all** evaluation!)

### Lazy List in Scala
**KRISHNA** In Scala, a list whose tail is evaluated only on a pull, that is, lazily, is a Stream.
It is a recursive structure like list.  Here is how you construct one.
```
val s1 = 1 #:: 2 #:: 3 #:: Stream.empty s1: scala.collection.immutable.Stream[Int] = Stream(1, ?)

val s2 = (1 until 10000).toStream //s2: scala.collection.immutable.Stream[Int] = Stream(1000, ?)
```

**KRISHNA** The operational behavior of Stream is different from that of a list, like you said, the
head is realized, whereas the tail is only realized when you ask for it...it is called into
being. Further the head of that tail is realized, whereas the tail of the tail again
is evaluated only when there is a pull.  The '?' mark stands for unevaluated stream.  So
these are all partially constructed streams until materialized.
```
s2.head //1000

s2.tail //Stream(1001, ?)
```

**KRISHNA** You can also force the entire stream to materialize by calling a `toList` on it.
```
val s3 = Stream(3, 2, 1, 0 , -1)
s3.toList //List(3, 2, 1, 0, -1)
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

**KRISHNA** You can transform the elements of a Stream using `map`, but the transformation won't happen
until evaluated.

```
val s4 = s3.map(_ * 2)
s4.take(3).toList  //List(6, 4, 2)
```

**KRISHNA** The virtue in being lazy is that - we not only allocate the space required, but also
can delay the computation until the demand is placed. Another name for lazy or pull-based collections 
is "non-strict" collections as opposed to "strict" or eager collections. 

### Lazy Sequences In Scala
**KRISHNA** Let's now see lazy sequences in Scala.
```
val l = Range(1, 10).toList.map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 5
    }
//mapping 1
//mapping 2
//mapping 3
//mapping 4
//mapping 5
//mapping 6
//mapping 7
//mapping 8
//mapping 9
//filtering 2
//filtering 4
//filtering 6
//filtering 8
//filtering 10
//filtering 12
//filtering 14
//filtering 16
//filtering 18
//List(2, 4)
``` 
**KRISHNA** Now with Streams...
```
val s = Range(1, 10).toStream.map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 5
    }

s.take(2).toList

//mapping 1
//filtering 2
//mapping 2
//filtering 4
List(2, 4)
```
**KRISHNA** So we saw how items flow one at a time in the pipeline.

**KRISHNA** In Scala, you can convert a "eager" collection to a "lazy" collection by using views.
 
```
val l = Range(1, 10).view.map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 5
    }
    
l.take(2).toList
``` 
**KRISHNA** Alternatively, you can force the view to evaluate everything using -

```
(1 to 10).view.map(_ * 2).force
```
**KRISHNA** This is lazy sequences, but lazy evaluation also applies to function arguments and 
in Scala and in Clojure, it is strict evaluation.  However, Haskell uses lazy evaluation
for all its function args by default.

### Infinitely lazy
**BRAMHA** coming soon...
(Ryan to fill this in..what do you think?...I know we don't have time, but it will
be helpful for workshop, but can do this at talk, if time permits)


#Infinitely Lazy in Scala
**KRISHNA** Lets say I have to build a list of 5 elements.  I would then write a `buildList` like this
```
def buildList(n: Int): List[Int] = {
  def buildListN(acc: List[Int], elem: Int): List[Int] = 
    if (elem < 0) acc else buildListN(elem :: acc, elem - 1)
  buildListN(Nil, n)
}

buildList(5)  //List(0, 1, 2, 3, 4, 5)
```
**KRISHNA** Here I have to think of base case to terminate the recursion.  Lets get a similar functionality using
streams.

```
def buildStream(from: Int): Stream[Int] = from #:: buildStream(from + 1)

val s = buildStream(0) //Stream(0, ?)
s.take(6).force //Stream(0, 1, 2, 3, 4, 5)
```

**KRISHNA** We take only what we are interested in and leave out the rest of the Stream unevaluated.

**KRISHNA** Lets say we want to have first 5 odd numbers from 32, then we could just do
```
buildStream(32).filter(x => x % 2 != 0).take(5).force
```
