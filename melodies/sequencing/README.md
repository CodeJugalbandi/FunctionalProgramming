# Sequencing

##
**BRAMHA** Lets say I want to extract and capitalize words that have size less than or 
 equal to 3 and I'll do this in Ruby, in an imperative style, with mutation of variables:

```
words = "all mimsy were the borogoves"
split_words = words.split("\s")
caps_words = []
split_words.each do |w|
  ;; enumeration and filtering
  cap-words.push(w.capitalize) if (w.size < 3)
end
words_s = caps_words.join("\n")
```

**BRAMHA** In the above code, enumeration and filtering are interleaved.  Let's try and
seperate eunumerations and filtering:

```
words = "all mimsy were the borogoves"
words.split("\s").map do |w|
  w.capitalize
end.filter do |w|
  (w.size < 3)
end.join ("\n")
```

**BRAMHA** This feels a bit more structured, we're chaining operations, one after the other. 
This style is called sequencing, where we're moving data down processing pipelines. 

**BRAMHA** In Clojure, we could chain functions together through just function nesting: 
```
(def words "all mimsy were the borogoves")
(def using-nesting
  (join "\n"
        (filter (fn [s] (< 3 (count s)))
                (map capitalize
                     (split words #"\s")))))
(println using-nesting)
```
**BRAMHA** But, parens is a code smell in Clojure/Lisp, so let's try and lose a few 
through function composition:

```
(def using-composition
  (let [f (comp
           (partial join "\n")
           (partial filter (fn [s] (< 3 (count s))))
           (partial map capitalize)
           (fn [s] (split s #"\s")))]
    (f words)))
(println using-composition)
```

**BRAMHA** The awkward thing about these styles of coding is the order of functions seem 
back to front. The Arrow syntax sidesteps composition in lieu of a threading-macro:

```
(def using-arrow
  (->>  (split words #"\s")
        (map capitalize)
        (filter (fn [s] (< 3 (count s))))
        (join "\n")))
(println using-arrow)
```

**KRISHNA** Lets see that how can we achieve the same in Scala. I'll first show using chaining...
```
val words = "all mimsy were the borogoves"

println(words
    .split(" ")
    .map(w => w.toUpperCase)
    .filter(w => w.length <= 3)
    .mkString("\n"))
```

**KRISHNA**  I'll define a few function types

~~~
  val split = (s: String) => s.split(" ")

  val capitalize = (ws: Array[String]) => ws.map(_.toUpperCase)

  val filter = (ws: Array[String]) => ws.filter(_.size <= 3)
    
  val join = (ws: Array[String]) => ws mkString "\n"
                                
  val words = "all mimsy were the borogoves"
  
  val splitCapitalizeFilterAndJoin = join compose filter compose capitalize compose split
  splitCapitalizeFilterAndJoin (words)
~~~

**KRISHNA** However the above is not flowing against the grain of thought.  Let me
have the cake and eat it too...I can make use of Scala's `andThen`
~~~
  val splitCapitalizeFilterAndJoin2 = split andThen capitalize andThen filter andThen join
  splitCapitalizeFilterAndJoin2 (words)
~~~

**KRISHNA** BTW, why is it called threading-macro in Clojure, when its a case of `andThen`?  
The threading word in there makes me think of process threads.
 
**BRAMHA** These are not process thread, they are there in the sense of stitching the 
functions, a sort of glue.

**KRISHNA** Oh I see!  Lets move on to the next melody.