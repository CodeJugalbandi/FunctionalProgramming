**BRAMHA** In FP languages, data structures don't behave in the way we’re used to in OO land. For instance, in Clojure:
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

**KRISHNA** also, a list is a function of its index.
~~~
val l = List("a", b", "c")
l(1)
~~~

**KRISHNA** Hey, what about functions, how are they defined? Lets say I want to define a function to square a number.  In Dr. Racket, I can define a square function that takes in a number like this
~~~
(define (square x) (* x x))
(square 4)
~~~

**KRISHNA**  Now if I want to see what `square` is - I get (lambda (a1) ...).
What this means is that `square`, though a function, is internally implemented
as a lambda - an anonymous function.
~~~
square

(lambda (a1) ...)
~~~

**KRISHNA**  Lets see how I can define the same function above in terms of lambda and
 then use it.
~~~
(define square2 (λ (x) (* x x)))
(square2 40)

square2

(lambda (a1) ...)
~~~

**BRAMHA**  This means all functions are 
internally implemented as lambdas and function syntax is just a syntactic sugar.  

**BRAMHA** You can also def a lambda, of course, in Clojure:
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

**BRAMHA** Under the hood there is not as much distinction between functions and lambdas as in OO. We can now map our lambda to a collection:
~~~
(map square [3 2 1])
~~~

**KRISHNA**  If you look at Scala, I can define a square function and map over it like this:
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
When I pass the method to `map`, the  Scala compiler converts 
it to the function value.  Or one could convert it like this: 

```
val square2 = squareMethod _
println(square2(2))
```

**BRAMHA** It is interesting to note that idiosyncrasy of Scala where the underlying 
implementation detail surfaces up and one needs to keep this in mind.  Whereas that is 
not the case with Racket, Clojure, the underlying implementation is the same and the 
function syntax is the sugar.

**KRISHNA** Somewhat similar is Groovy as well.  You can convert a method into a
closure by using the & operator.
```
def square(x) { x * x }
println(square(2))

def squareClosure = { it * it }

def squareClosure2 = this.&square
```

**BRAMHA** In Haskell, a lambda looks like this:
~~~
(\x -> x * x) 2
-> 4
~~~

**BRAMHA** A function would look like this:
~~~
square x = x * x
square 3
-> 9
~~~

**KRISHNA** When exploring a language from a FP point of view, it's interesting to see how lambda is expressed - e.g. is it a first class citizen or syntactic sugar?
Did lambda's arrive late in the language's evolution, or right from the start?
