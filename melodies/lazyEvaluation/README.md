## Laziness
**BRAHMA** In many languages arguments passed to a function are evaluated before calling the function.  For example, eager evaluation in Java would look like

```
class Eager<T> {
  private final T value;
  
  Eager(final T value) {
    System.out.println("eager...");
    this.value = value;
  }

  public T get() { 
    return value; 
  }
}
```

**BRAHMA** Lets say we have function twice that multiplies input value by 2.

```
Integer twice(Integer n) {
  System.out.println("twice...");
  return 2 * n;
}

Eager<Integer> eager = new Eager(twice(3)); 
// twice… 
// eager…

eager.get(); // 6
```

**BRAHMA** Is it possible to defer the evaluation of the argument, until we actually need it? It turns out that you can do so in Java8 using a Lambda.  By using lambda to wrap an argument, one can turn a strict evaluation into a lazy one.

```
class Lazy<T> {
  private final Supplier<T> value;
  
  Lazy(final Supplier<T> value) {
    System.out.println("lazy...");
    this.value = value;
  }
  public T get() { 
    return value.get(); 
  }
}

Lazy<Integer> lazy = new Lazy(() -> twice(3)); // lazy…

lazy.get(); 
// twice… 
// 6
```

**BRAHMA** When we need to evaluate, we apply the lambda in the `get()` method.  In general, this technique can be readily applied to a language that supports passing functions and does not have lazy evaluation baked into the language itself, thus *simulating* laziness.

### Laziness in Clojure
**KRISHNA** Clojure is pretty much same like Java8 here.

```
(defn twice [n]
  (do
    (println (str "twice called with n = " n))
    (* 2 n)))

(defn get-value [value] value)

(get-value (twice 2))
; twice called with n = 2
; 4

; wrap argument in lambda
(get-value (fn [] (twice 2)))
; #<user$eval72$fn__73 user$eval72$fn__73@51bf5add>

; run it
((get-value (fn [] (twice 2))))
; twice called with n = 2
; 4

```
**KRISHNA** When using this, the developer has to take care and make sure that values are wrapped in lambda for lazy evaluation to work, forgetting which can give unpredictable results at run-time.

### Laziness in Scala
**BRAHMA** In Java8 or Clojure, one has to explicitly wrap the argument in a lambda, whereas Scala offers it as a part of syntax and is called as a call-by-name parameter:

```
class Lazy[T](value: => T) {
  def get = value
}

def twice(n: Int) = { 
  println ("twice...")
  2 * n
}

val lazi = new Lazy(twice(2))

lazi.get 
// twice... 
// 4

lazi.get 
// twice... 
// 4
```

**BRAHMA** Additionally, Scala also implements call-by-need lazy evaluation strategy through the `lazy` keyword that does not evaluate upon initialization, but evaluates only when the value defined by `lazy` is first needed.  Once evaluated, the result of the evaluation is cached for subsequent uses. 

```
class Lazy[T](value: => T) {
  lazy val get = value
}

val lazi = new Lazy(twice(2))

lazi.get
// twice...
// 4

lazi.get
// 4
```
**BRAHMA**  Thus, with call-by-need, we can further optimize the performance as the evaluation results are shared, whereas with call-by-name, the evaluation is repeated upon each call.  As long as we use pure functions, call-by-name and call-by-need will yield same results, but with side-effecting functions, call-by-name and call-by-need results may differ.

### Laziness in Haskell
**KRISHNA** Many functional programming languages support laziness, however Haskell is inherently lazy.  You don't need any special syntax or keyword to mark an argument for lazy evaluation.  All arguments are by default - lazy! and hence is also known as a lazy functional language.

```
import Debug.Trace

twice :: Int -> Int
twice n = trace ("twice called with input: " ++ show n) 
          2 * n

lazy :: Int -> Bool -> Int
lazy n doTwice | doTwice   =  n
               | otherwise = -1

main :: IO ()
main = do
  print (lazy (twice 2) True)   
  -- twice called with input: 2 
  -- 4
  print (lazy (twice 2) False)  
  -- -1
```

**KRISHNA** In Haskell, you will need to be explicit to get strict evaluation to work.  See https://wiki.haskell.org/Performance/Strictness for further information.

## Reflections
**BRAHMA** The virtue in being lazy is that - we not only allocate the space required, but also can delay the computation until the demand is placed. This saves CPU cycles and reduces memory footprint for computating results that may never end-up being used.

**KRISHNA** Haskell is the laziest of all the languages we looked at so far, followed by Scala, Clojure and Java in that order of decreasing laziness.

**BRAHMA** Also, notice that one has to work hard to achieve laziness in a languages (like Java8, Clojure) that does not have it out-of-box, that is, wrap in a lambda and in languages like Haskell, where lazy parameters are a default, one has to work hard to be eager.  With Scala, we have the what I'd like to call - optionally lazy, where though the default is eager, but by you can bring in laziness (either call-by-name or call-by-need) by using syntax or the keyword `lazy`
