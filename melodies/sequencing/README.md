# Sequencing

**BRAMHA** Lets say I want to extract and capitalize words (from a collection of words) that have size less than or equal to 3.

In Ruby, in an imperative style, with mutation of variables, it would look like this:

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
seperate eunumeration and filtering:

```
words = "all mimsy were the borogoves"
words.split("\s").map do |w|
  w.capitalize
end.filter do |w|
  (w.size < 3)
end.join ("\n")
```

**BRAMHA** This feels a bit more structured, we're chaining operations, one after the other. 
This style is called `sequencing`, where we're moving data down processing pipelines. 

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
**BRAMHA** This is hard to understand. Deep nesting leads to many parentheses in Clojure: considered a code smell in Clojure. Let's try and lose a few parens through function composition:

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

**BRAMHA** The awkward thing about these styles of coding is that the order of functions seem 
back to front. The `arrow` syntax sidesteps composition in lieu of a threading-macro:

```
(def using-arrow
  (->>  (split words #"\s")
        (map capitalize)
        (filter (fn [s] (< 3 (count s))))
        (join "\n")))
(println using-arrow)
```

**KRISHNA** BTW, why is it called a `threading-macro` in Clojure? The threading word in there makes me think of process threads.
 
**BRAMHA** The word 'threading' is used in the sense of threading a needle through cloth, not to be confused with process threads! Since it is implemented as a macro, is is called a threading-macro.

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

**KRISHNA** Just like in your clojure nesting and composition examples, this is flowing against the grain of thought.  Let me
have the cake and eat it too. I can make use of Scala's `andThen`
~~~
  val splitCapitalizeFilterAndJoin2 = split andThen capitalize andThen filter andThen join
  splitCapitalizeFilterAndJoin2 (words)
~~~

**BRAHMA** In whatever functional programming language you're using, if you find yourself deeply nesting functions, you can be sure there is a better way of "threading" functions together. Moreover, if you are going against the "grain of thought", look for a way in the language to go with the grain!

**KRISHNA** This style is also known as concatenative programming where function composition is the default way to build subroutines. Functions written in concatenative style neither represent argument types nor the names or identifiers they work with; instead they are just function names laid out as a pipeline, in such a way that the output type of one function aligns with the input type of the next function in the sequence. Thus, the order of function application gets transformed into order of function composition. 

**BRAMHA** One of the immediate benefits of this style is that it makes the implementation very succinct, thus very helpful in reasoning about the program.  Secondly, these small referentially transparent functions can be refactored away into a new sub-sequence, thus improving the understanding of the domain.
