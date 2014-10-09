
# Recursion Melody Script #

**Bramha**: (Talking to the audience and Krishna) okay, in this melody I am going to use recursion to calculate sum of elements in a list.
I'll be coding in Ruby.  

Bramha, then codes along and shows linear recursion and shows 
~~~
  Recursive Process
  sum_rec([2 3 5])
 
  first it EXPANDS!
   -> builds chain of deferred operations
  2 + sum_rec([3 5])
  2 + (3 + sum_rec([5]))
  2 + (3 + (5 + sum_rec[]))
  2 + (3 + (5 + 0)) -> only primitives!
 
  then it starts to FOLD back in
  2 + (3 + 5)
  2 + (8)
  10
 
  folds back from the deepest inner nest
  back outwards
 
  ------- Space -------->
  ====================================
  sum_rec([2 3 5])
  2 + sum_rec([3 5])
  2 + (3 + sum_rec([5]))
  2 + (3 + (5 + sum_rec[]))
  2 + (3 + (5 + 0))        -> only primitives!
  2 + (3 + 5)
  2 + (8)
  10
 
  use of SPACE grows linearly with 'arr' size
  * LINEAR recursion
~~~

and now turns the gaze to Krishna.  Krishna gets the signal and says

**Krishna**: Okay, Let me see how I can write recursion a bit differently.

Krishna then takes to keyboard and starts coding in Racket (a Scheme dialect)

~~~
   (define L (list 2 3 5))

   (define (sum lst) 
     (local [(define (iterate acc l) 
               (if (empty? l) 
                   acc
                   (iterate (+ acc (first l)) (rest l))))]
       (iterate 0 lst)
       ))

   (sum L) 
 
  ------ Space ------>
  
  (sum 0  [2, 3, 5])  |
  (sum 2  [3, 5])     |
  (sum 5  [5])        | Time
  (sum 10 [])         |
  10                  V
~~~

Now, Krishna is also done and says that with this recursion definition the process space does not grow and shrink
and it stays the same, while the time is still proportional to the number of elements in the list. This is an
iterative process.
Now both Bramha and Krishna get into conversation to reflect upon what each one did (as it may not be possible to
show the code simultaneously, show the slide instead - see presy folder)

## Conversations ##
**Bramha**: It is interesting to note that though both are recursive definitions, the shape of the process that
they have generated is quite different.  In the first one the space complexity is proportional to the number of elements, while
in the second one, its at constant space.  

**Krishna**: So, though the definitions are recursive does not matter, it is the shape of the process that they generate and
that is what defines as being an iterative process or a recursive process.

**Bramha**: True, there is nothing in the definition that tells you that it will lead to recursive or iterative process.

**Krishna**: Also, I observe that all the information needed by next iteration is available in the formal parameters of
the function, while in the recursive process, partial information is available in the formal parameters of the function and
the remaining information is parked under the table in the computer.

**Bramha**: Hmmm, thats very interesting....hypothetically, if we were to kill iteration in between, we can still resume from
some left over step, where as in recursive process, due to its information spread in 2 different areas, it would not be possible
to recover from failure as the space that holds this information may not be accessible or outright dead.

**Krishna**: True, and further, when playing with large list sizes, the space under the table in the computer is going to increase
and at some point we would see an overflow from that space.

**Bramha**: Yep true and so I think, its important to feel the shape of the process whether you write recursive code or not.  
Also, by writing recursive definitions alone does not mean that the shape of the process it recursive.

**Krishna**: True, its worth noting these observations.

**Krishna**: Also, on a stack-based computer, though its the shape of iterative process, the recursion will also blow-up, simply because the
recursive calls will still use the stack space, for nothing!  Lets write this recursive definition for iterative process in Groovy.  Krishna
then writes this piece of code and shows stack overflow.

~~~
def iterate(acc, list) {
  if (list.isEmpty()) 
	 acc 
  else 
	 iterate(acc + list.head(), list.tail())
}

def sum(list) {
  iterate(0, list)
} 

list = 1..5000
println (sum(list))

Output:
java.lang.StackOverflowError
	at ConsoleScript22$iterate$0.callCurrent(Unknown Source)
	at ConsoleScript22.iterate(ConsoleScript22:6)
	at ConsoleScript22$iterate$0.callCurrent(Unknown Source)
	at ConsoleScript22.iterate(ConsoleScript22:6)
	at ConsoleScript22$iterate$0.callCurrent(Unknown Source)
	at ConsoleScript22.iterate(ConsoleScript22:6)

~~~

**Bramha**: True, whereas if we take the non-recursive version in Ruby. 

Bramha now writes sum which is purely iterative and says....

~~~
def sum_iter(arr)
  acc = 0
  arr.each {|num| acc += num}
  acc
end

sum_iter([2 3 5])
# acc = 0
# acc += 2  ->  2
# acc += 3  ->  5
# acc += 5  ->  10

# acc is CHANGED IN PLACE (mutation)
# acc has a LIFECYCLE (on a timeline)

~~~

**Bramha**: Look this one also runs in a constant space, however, we end up using in-place mutation of the total variable.

**Krishna**: Thats right! Hey Bramha, what if we could have our cake and eat it too, by that I mean, what if we still were able to write recursive definition for iterative process and somehow there is no blow-up?

**Bramha**: Thats where we need to push this to compiler and let it translate the recursion definition of a iterative process to run in constant space.

**Krishna**: Yes, I know of the Scala compiler doing that and let me show you that.

Krishna writes the same code in Scala and shows that the recursive definition does not blow up.

~~~
def sum(xs : List[Int]): Int = {
  def iterate(acc: Int, ls: List[Int]): Int = 
    if (ls.isEmpty) 
      acc 
    else 
      iterate(acc + ls.head, ls.tail)
  
  iterate(0, xs)
}

sum(Range(0, 5000).toList)
sum(Range(0, 50000).toList)
sum(Range(0, 500000).toList)

~~~ 

**Bramha**: Yes, this is called as Tail Recursion.  Where-ever the last line in a procedure is a call in the tail position, constant
space optimization can be achieved for recursive definition.

**Krishna**: Yay, we had the cake and ate it too!!

**Bramha**: Ha, Ha, Ha....Yes!

**Bramha**: Hey, now that we have sum using iterative process, can we count the elements
in the list using it?  Let me give that a shot using Clojure

~~~
(defn count-iter [acc arr]
  (if (= [] arr)
    acc
    (recur (+ 1 acc) (rest arr))))

(defn count- [arr]
  (count-iter 0 arr))

(count- [2 3 5])
~~~

**Krishna**: I'll just re-write this in Scala
~~~
def count(xs : List[Int]): Int = {
  def iterate(acc: Int, ls: List[Int]): Int =
    if (ls.isEmpty)
      acc
    else
      iterate(acc + ls.head, ls.tail)

  iterate(0, xs)
}

count(Range(0, 5000).toList)
count(Range(0, 50000).toList)

~~~

**Krishna**: Hey, look we have duplication of the `iterate` function here.  We need
to get rid of this smell.


**Bramha**: What if, we extract out the iterate function and to pass a function that
does the above?  Let us give this a shot.

~~~
(defn reduce- [f acc arr]
  (if (= [] arr)
    acc
    (recur f
           (f acc (first arr))
           (rest arr))))

~~~

**Bramha**: Now I can express the above `sum-iter` and `count-iter` in terms of the
`reduce-`

~~~
(defn count-iter-2 [arr]
  (reduce- (fn [acc e] (+ 1 acc))
           0
           arr))
(defn sum-iter-2 [arr]
  (reduce- +
           0
           arr))
~~~

**Krishna**: I'll play this in Scala.  As Scala is typed, I need to give represent the
above as a function that consumes 2 parameters, accumulator and item and produces a
result that is also an Int (Int, Int) => Int

~~~
def iterate(initialValue: Int, xs: List[Int])(f: (Int, Int) => Int): Int =
  if(xs.isEmpty)
    initialValue
  else
    iterate(f(initialValue, xs.head), xs.tail)(f)

def count(xs: List[Int]) : Int =
  iterate(0, xs) { (accumulator, elem) =>
    accumulator + 1
  }

def sum(xs: List[Int]): Int =
  iterate(0, xs) { (accumulator, elem) =>
     accumulator + elem
  }
~~~

**Krishna**: Now, writing product of the elements of the list becomes very easy.
~~~
def product(xs: List[Int]): Int =
  iterate(1, xs) { (accumulator, elem) =>
     accumulator * elem
  }
~~~

They both look at each other and the audience with a smile and come forward on the stage and in a bow-down gesture indicate that the
melody round is complete.