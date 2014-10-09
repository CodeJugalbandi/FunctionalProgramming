# Destructuring + Pattern Matching

## Destructuring

**BRAMHA** In FP, we favour simple data structures over classes, e.g. instead of a collection of people objects, 
we might have a collection of 'person' tuple, containing only the fields we're interested in:

```
type Person = (String, Int, String) //Tuple with 3 elements

val p = ("alan", 30, "spiders")
```

**BRAMHA** ...and then we have some functions acting on this 'person' data 'type'.  Whatever we'll 
do with 'person', we'll be needing code to get at the innards of the 'person' tuple.

```
def showPerson(p: Person) = p match {
   case (name, latitude, fears) => println(s"$name - $latitude - $fears")  
}

showPerson(p) //alan - 30 - spiders
```

**BRAMHA** How does this look in Clojure?  

**KRISHNA** Most functional languages provide some level of 'destructuring':

```
(def a-person ["alan" 30 "spiders"])

(defn print-person [p]
  (println (first p) " " (second p) " " (p 2)))
  
(defn print-person2 [[name latitude primal-fear]]
  (println name " - " latitude " - " primal-fear))
```

**KRISHNA** Destructuring gives you a shorthand way of naming parts of a structure.  If we were using a hash-map 
to represent our person:

```
(def h-person {:name "alan"
               :latitude 30
               :fear "spiders"})

;; destructuring hash-maps looks like this:
(defn print-person3 [{:keys [name latitude fear]}]
  (println name " - " latitude " - " fear))
```

**KRISHNA** There are libraries that allow for even more compact and sophisticated destructuring syntax, 
but let's leave it at that for destructuring in Clojure...

**BRAMHA** This is interesting, I am not aware of de-structuring Maps in Scala, out-of-the-box.  However, you can 
de-structure Lists, Vectors and Streams in Scala.  If Person was a Vector like you showed in Clojure..I could Just
do the following
```
val p = Vector("alan", 30, "spiders")

def showPerson(p: Vector[Any]) = p match {
   case Vector(name, latitude, fears) => println(s"$name - $latitude - $fears")  
}

showPerson(p) //alan - 30 - spiders

```

## Pattern Matching

**BRAMHA** So far we've just talked about picking apart the structure of things, 
but we can generalise de-structuring into something even more powerful: **Pattern Matching** (not to 
be confused with string pattern matching)

**BRAMHA** Let's explore this by example: let's say we have a journal with a few fields, 

```
import java.util.Date

case class Journal(op: String, amt: Double, date: Date, metaData: String)
```

**BRAMHA** Lets say, we want to save the journal entries like debit and credit...

```
def save(j: Journal) = j match {
    case Journal("debit", amt, date, metaData)  => println(s"DEBIT $amt $date $metaData")
    case Journal("credit", amt, date, metaData) => println(s"CREDIT $amt $date $metaData")
    case Journal(unknown, _, _ , _) => println(s"Do I know op $unknown?")
}

save(Journal("debit", 23, new Date, "invoice"))
save(Journal("credit", 10, new Date, "payment"))
```

**KRISHNA** There is difference between De-structuring and Pattern Matching.  In this case, we're not only 
matching on relative position of elements in the data structure, we're trying to dispatch to different 
functions based on a value inside the data structure! This dispatch is a kind of conditional construct, while 
with destructuring there is nothing conditional about it.

**BRAMHA** We get pattern matching in Scala, for free, by using case classes.  Scala compiler implicitly adds a
companion object for the case class and provides an implementation for the apply() method (basically
factory method to create the classes) and an unapply() method to get the constituents back.  

**BRAMHA** Pattern matching in Scala, essentially is a generalization of switch statements in Java, where we used 
Ints, Enums and Strings, but now we can use Class Hierarchies ...just that the syntax is now different, 
using the keyword - match.
```
selector match {
  case pat1 => expr1
  ...
  ...
  case patN => exprN
}
```   

**BRAMHA** Yes, I see, we're wanting to pick only those journals where the first value is :debit or :credit. So, 
you can't do this kind of thing in Clojure?

**KRISHNA** Idiomatic Clojure doesn't have "Pattern Matching" in that way. But, there is a way to achive this (there are libraries that implement sophisticated Pattern Matching)

```
(def debit-journal [:debit 25 "1 Oct 2014" "invoice"])
(def credit-journal [:credit 26 "3 Oct 2014" "payment"])
```
Then one can use multi-methods in Clojure like so:
```
(defmulti save-j (fn [j] (first j)))

(defmethod save-j :debit [j]
  (println "DEBIT: " j))
(defmethod save-j :credit [j]
  (println "CREDIT: " j))

(save-j debit-journal)
(save-j credit-journal)
```

**KRISHNA** Multimethods allow for dispatching on the args in a very flexible way. 

**KRISHNA** In Haskell, this would have been expressed much more directly:
```
save-j ('debit' : amt : meta) = ...
save-j ('credit' : amt : meta) = ...
```
