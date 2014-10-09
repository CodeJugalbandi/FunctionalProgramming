# The expression problem in FP

### Classes translate to...

**BRAMHA** In OO, we model our domain concepts by creating our own types (using classes and interfaces).
How do we do this in FP?  Let's model the geometric shapes Circle and Rectangle in Clojure

```
(defrecord Circle [r])
;; this defines a Java class with public final field 'r', i.e. Circle is an immutable value. 

;; I can create a new Circle like this:
(Circle. 11)

;; and I can access the field asif it was a key/value in a hashmap.
(:r (Circle. 11))

;; Similarly:
(defrecord Rect [l w])
```

**BRAMHA** Classes in OO act as containers for structured data, as well as behaviour acting on the contained data.
In Clojure, "records" take care of just the structured-data containers...

### Interfaces translate to…

**BRAMHA** For each shape, we want to define area + perimeter.  In Clojure, the corollary of an Interface 
is a Protocol:

```
(defprotocol Shape
  (area [this])
  (perimeter [this]))
```

**BRAMHA** Now, we can extend our Circle/Rect type to implement the Shape protocol

```
(extend-type Circle
  Shape
  (area [circle]
    (* (:r circle) (:r circle) Math/PI)))

(extend-type Rect
  Shape
  (area [rect]
    (* (:l rect) (:w rect))))
```

**BRAMHA** Now we have simple type-based dispatch for our protocol method 'area'

```
(area (Circle. 10))
(area (Rect. 10 20))
```
(this is a limited form of polymorphic dispatch - Clojure has more powerful but less performant polymorhic dispatch through 'multimethods')

### The Expression Problem (Clojure)

**BRAMHA** Now, let's add a new type that implements our existing Shape protocol:

```
(defrecord RightTriangle [b h]
  Shape
  (area [this]
    (/ (* (:b this) (:h this)) 2)))

(area (RightTriangle. 10 100))
```
**BRAMHA** By doing this, we've satisfied the first part of what is called the "Expression Problem" (Phil Wadler '98):

```
(1) be able to create new types to implement existing interfaces
This is hardly impressive, all OO languages allow you to create new classes to implement existing interfaces.
```
**BRAMHA** The second part of the Expression Problem is more rare to see in languages:

```
(2) be able to implement a new interface for an existing type
```
**BRAMHA** To achieve (2) with Clojure we can do this:
```
(defprotocol Graphic
  (draw [this]))

(extend-type RightTriangle
  Shape
  (draw [this] (puts "***")))
```

**BRAMHA** Moreover (1) and (2) should be possible without having to recompile the code!
This allows for much more flexibility in working with 3rd party code that you 
cannot recompile.

### Expression Problem - 'rows and cols' perspective

**BRAMHA** A useful way of looking at it is: 

```
               area     perimeter     draw
Circle          x          x
Rect            x          x
RightTriangle   x          x          x
```
**BRAMHA** In the Expression problem, (1) is about adding rows (new types), 
and (2) is about adding columns (new operations). FP langs tend to solve (2) 
better than OO langs.  Dynamic languages like Ruby mitigate the expression problem 
through "monkey patching" as they have "open classes".  Monkey Patching is 
where you add or replace existing methods, but one would not know whether
such method pre-existed before and there is a name clash.  The very flexibility
that these languages offer results in uncertainty.

**KRISHNA** Lets try to solve the expression problem in Scala. 
~~~
trait Shape
case class Circle(r: Int) extends Shape
case class Rectangle(l: Int, w: Int) extends Shape
~~~

**KRISHNA** Now , let me define set of operations using `trait ShapeOperations[T]`
that I can perform on these shapes - area and perimeter.  If you observe this is
a type class in scala with T as the type parameter.  I then define 2 
implicit object implementations, one for Circle and another for Rectangle that
 do the respective calculations for area and perimeter. 
~~~
trait ShapeOperations[T] {
  def area(t: T): Double
  def perimeter(t: T): Double
}

implicit object CircleOperations extends ShapeOperations[Circle] {
  def area(c: Circle): Double = Math.PI * c.r * c.r
  def perimeter(c: Circle): Double = 2 * Math.PI * c.r
}

implicit object RectangleOperations extends ShapeOperations[Rectangle] {
  def area(r: Rectangle): Double = r.l * r.w
  def perimeter(r: Rectangle): Double = 2 * (r.l + r.w)
}

def area[T](t: T)(implicit o: ShapeOperations[T]): Double = o.area(t)
def perimeter[T](t: T)(implicit o: ShapeOperations[T]): Double = o.perimeter(t)
~~~

**KRISHNA**  Finally, I define 2 methods area and perimeter in terms of operations 
defined on type classes and these methods have no knowledge of what the `OperationsFor[Shape]`
are defined.  Also, the parameter `o` is passed as `implicit` so that the compiler can
pick up the appropriate value of `implicit object` in the current scope for that 
type of `T` if its defined.  However, this does not mean that users cannot pass explicit operations.
If they do pass an explicit one, then that will override the implicit values that are
in scope.

I'll just put the above to work here
~~~
val r = Rectangle(2, 3)  
area(r)   //6.0
perimeter(r) //10.0
  
val c = Circle(2)
area(c)  //12.566370614359172
perimeter(c) //12.566370614359172
~~~

**KRISHNA** Let now add a new Operation, say draw.  This is tough in OO,
especially without modification (i.e. no recompile).  Like before, I'll
define another typed `trait Graphics[T]` and then provide implementations 
for Circle and Rectangle.  
~~~
trait Graphics[T] {
  def draw(t: T): Unit
}

implicit object CircleGraphics extends Graphics[Circle] {
  def draw(c: Circle) = println(s"Drawing Circle with ${c.r}...")
}

implicit object RectangleGraphics extends Graphics[Rectangle] {
  def draw(r: Rectangle) = println(s"Drawing Rectangle with Length = ${r.l}, Width = ${r.w}...")
}

def draw[T](t: T)(implicit g: Graphics[T]) = g.draw(t)
~~~

**KRISHNA** If you observe I only write code for the new implementation of `draw`
in both the cases and yet I don't recompile.  I also define another method `draw` 
that consumes a `t` and an implicit `Graphics[T]` object.  Again, this method is 
defined in terms of `g` and its value will be picked up from whatever the scope 
is supplied. If compiler does not find any implicit in the scope, it tries 
searching in the companion object of the type class trait and if it does not 
find there either, it will report an error that it could not find the match 
for that implementation of Graphics[T].  Lets put this one to work now.
~~~
draw(c)  //Drawing Circle with 2...
draw(r)  //Drawing Rectangle with Length = 2, Width = 3...
~~~

**KRISHNA** Lets now add a new type.  This time i'll add RTriangle and create
an implicit object that implements all its methods
~~~
case class RightTriangle(b: Int, h: Int) extends Shape

implicit object RightTriangleOperationsAndGraphics 
  extends ShapeOperations[RightTriangle] with Graphics[RTriangle] {
    def area(rt: RightTriangle) = 0.5 * rt.b * rt.h
    def perimeter(rt: RightTriangle) = rt.b + rt.h + (Math.sqrt(rt.b * rt.b + rt.h * rt.h))
    def draw(rt: RightTriangle) = println(s"Drawing RTriangle with Breadth = ${rt.b} and Height = ${rt.h}...")
}
~~~

**KRISHNA**  Lets put this to work now...
~~~
val rt = RTriangle(2, 3)

area(rt)      //3.0
perimeter(rt) //8.60555127546399
draw(rt)      //Drawing RTriangle with Breadth = 2 and Height = 3...
~~~

**KRISHNA** So this is how using type classes in Scala and using Generics in Java
and without recompile we solved the expression problem.   Also, one thing that I 
observe it that in Clojure, though Shape is a protocol (like interfaces), why
did you not implement the perimeter method?

**BRAMHA** They are optional so that you can define them later, Clojure gives
you that flexibility

**KRISHNA** But then the `defprotocol` is violating protocol...meaning interfaces
are contracts to be respected by the implementing class, they are not supposed to
be optional.

**BRAMHA** Well in Clojure protocol in that sense is a loose contract.

**KRISHNA** Ok i see what you mean.

### The Expression Problem (Haskell)
**BRAMHA** Let see this in Haskell, the above situation looks very close 
to the Clojure code. First we define the data:

```
data Shape = Circle Float
            | Rect Float Float
            | RTriangle Float Float


data Circle = Circle {radius :: Float}
data Rect = Rect {width :: Float, length :: Float}
let rect = Rect {width = 10, length = 20}
```

**BRAMHA** Then the type class (protocol/interface)

```
class Shape where
  area :: Shape -> Float
  perimeter :: Shape -> Float
```
Then we extend the types to implement the new typeclass

```
instance Shape Circle where
  area (Circle r) = PI * r * r
  perim (Circle r) = 2 * PI * r

instance Shape Rect where
  area (Rect l w) = i * w
  perim (Rect l w) = 2 * (l + w)
```
