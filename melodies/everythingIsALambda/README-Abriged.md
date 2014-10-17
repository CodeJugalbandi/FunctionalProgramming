**BRAMHA** In FP languages, data structures don't behave in the way we’re used to in OO land:
~~~
(def h {:x 123 :y 456})
(h :x)
-> 123
~~~

**BRAMHA** a hash-map is a function of it’s keys. In the same way, a Vector (Array) is a function of it’s index:
~~~
(def arr ["a" "b" "c"])
(arr 1) -> "b"
~~~

**KRISHNA** In Scala too, a map is a function of its key
~~~
val m = Map('x' -> 123, 'y' -> 456)
m('x') //123
~~~

**KRISHNA** and so is list a function of its index.
~~~
val l = List("a", b", "c")
l(1)
~~~

**BRAMHA** Hey, what about functions, how are they defined? Lets say I want to define a function 
to square a number.
~~~
(def square-lambda 
  (fn [num] (* num num)))

(square-lambda 2)
~~~

**BRAMHA** With sugar added:
~~~
(defn square [num] 
  (* num num))
~~~

**BRAMHA** But this is just sugar, and there is not as much distinction between functions and values as in OO. 

We can map lambdas to collections, 
~~~
(map square [3 2 1])
~~~

**KRISHNA**  If you look at Scala, I can define a square method as and thats its signature.
~~~
def squareMethod(x: Int) = x * x

squareMethod(2)

List(3, 2, 1).map(squareMethod)
~~~

**KRISHNA** However, I can define a Function as object as well, such as square 
~~~
val square = (x: Int) => x * x 

square(2)

List(3, 2, 1).map(square)
~~~

**KRISHNA** If I do this now
~~~
squareMethod.getClass
error: missing arguments for method square;
follow this method with `_' if you want to treat it as a partially applied function
              squareMethod.getClass
              ^

square.getClass
// Class[_ <: Int => Int] = class $anonfun$1
~~~

**KRISHNA** So in Scala, we can distinctly see the type form as `val` meaning 
Function as an object and the action form as `def` meaning a method.  
When I pass the method to like `map`, the  Scala compiler converts 
it to the function value.  Or one could convert it like this: 

```
val square2 = squareMethod _
println(square2(2))
```

**BRAMHA** It is interesting to note that idiosyncrasy of Scala where the underlying 
implementation detail surfaces up and one needs to keep this in mind.  Whereas that is 
not the case with Racket, Clojure, the underlying implementation is the same and the 
function syntax is the sugar.

**BRAMHA** Haskell is a very succint functional language, so it's worth looking at:

**BRAMHA** In Haskell, a lambda looks like this:
~~~
(\x -> x * x) 2
-> 4
~~~

**BRAMHA** If I wanted to write a function:
~~~
square x = x * x
square 3
-> 9
~~~

**KRISHNA** When exploring a language from a FP point of view, it's interesting to see how lambda is expressed - e.g. is it a first class citizen or syntactic sugar?
