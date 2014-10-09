# Referential Transparency

**BRAMHA** Lets say we want to create a calculator that adds and adds to memory and
now I can define it in say Java as:
~~~
class Calculator {
  private Integer memory = 0
  
  public Calculator(Integer memory) {
    this.memory = memory;
  }
  
  public Integer recallMemory() {
    return memory;
  }

  public void add(Integer x, Integer y) {
    return x + y;
  }
  
  public Integer memoryPlus(Integer n) {
     memory = add(memory, n);
     return memory;
  }
}
~~~

**BRAMHA** Lets observe the Java calculator...To hold the state of memory, 
I need to define an instance variable and the memoryPlus method mutates that variable
each time it is called.  So this method reaches out-side of itself and mutates memory.
Whereas, if you see, the add method, it just operates on the input variables, x and y.
It neither modifies the inputs nor "reaches-out" to any state variable outside of it
self.  So, add is a pure function, it just works on the inputs provided and always will
return the same output for same set of inputs....guaranteed.  Whereas with memoryPlus 
that will not be the case. It is not "pure" and has "side-effect".  So memoryPlus is
referentially opaque, whereas add is referentially transparent.

**KRISHNA** If I were to implement this functionality in Racket (a Scheme dialect), I 
will need to define a structure for calculator as:
~~~
;I define a structure for calculator
(define-struct calculator (memory) )

; The following new functions are then automatically available. 
; Constructor for constructing a calculator
(make-calculator 0)

; Lets capture that in C
(define C (make-calculator 0))

; Selector
(calculator-memory C)

; Predicate
(calculator? C)
(calculator? "Hello")
~~~

**KRISHNA**  But give me any mutators for the struct definition (Dr. Racket does not
in the Intermediate Student mode.)

~~~
; Now I can define add simply as:
(define (add x y) (+ x y))
~~~

**KRISHNA** As I don't have mutator available to me, I am forced to do make a new 
calculator after I compute the results of addition.
~~~
(define (memory-plus calc n) 
  (make-calculator (add (calculator-memory calc) n)))

(define C2 (memory-plus C 2))
(calculator-memory (memory-plus C2 2))

(define C3 (memory-plus C2 2))
(calculator-memory (memory-plus C3 2))
~~~

**KRISHNA**  Interestingly, I never mutate, but I create a new one each time there
is a state change and give back a new calculator.  Both the functions add as well
 as memory-plus are pure and referentially transparent.
  
**BRAMHA** Lets say me refactor the calculator code to make all the 
functions referentially transparent, i'll have to return a new calculator
each time from the memoryPlus method.

~~~
class Calculator {
  private Integer memory = 0
  
  public Calculator(Integer memory) {
    this.memory = memory;
  }
  
  public Integer recallMemory() {
      return memory;
  }

  public Integer add(Integer x, Integer y) {
    return x + y;
  }
  
  public Calculator memoryPlus(Integer n) {
     return new Calculator(add(memory, n));
  }
}
~~~

**BRAMHA** So that was interesting aiye...now I have made the memoryPlus
method Referentially transparent.  

**KRISHNA** you say that it is ref trans because there is no mutation, but 
I would say it is *not* ref trans because it depends on 'memory', which 
is "out of band". so, the function is still not pure because it may return 
different results in different contexts. Say, if someone introduces a method 
in this class and it mutates the memory, then in that case `memoryPlus` will 
return different answers based on the state of memory.   
 
**BRAMHA** So, I must then make memory a constant.
 ~~~
 class Calculator {
   private final Integer memory
   
   public Calculator(Integer memory) {
     this.memory = memory;
   }
   
   public Integer recallMemory() {
       return memory;
   }
 
   public Integer add(Integer x, Integer y) {
     return x + y;
   }
   
   public Calculator memoryPlus(Integer n) {
      return new Calculator(add(memory, n));
   }
 }
 ~~~

**KRISHNA** So, the observation is, referentially transparent methods are context-free.
We can use them in one context or the other and neither the meaning of the context
get altered, nor their meaning itself.  And second observation, to be referentially
transparent, method will require to work with immutable data.

**BRAMHA** How about if I completely remove state out of Calculator?  This time i'll 
use Scala, so it becomes light on boiler-plate.  I'll make Calculator a Data Class using
the case class construct of Scala.  Also, I get a copy method for free.  It lets me make 
a copy of an object by changing the fields as desired during the copying process. 

~~~
case class Calculator(val memory: Int)

object Calculator {
  def add(x: Int, y: Int) = x + y
  
  def memoryPlus(c: Calculator, n: Int): Calculator = {
     c.copy(memory = add(c.memory, n));
  }
}

scala> val c = Calculator(0)
scala> c.memory  //0

scala> val c2 = Calculator.memoryPlus(c, 3)
scala> c2.memory  //3
~~~

**KRISHNA**  Let me do the complete opposite and introduce a mutator and this time. 
(and he switches to Advanced Student mode in Dr. Racket).

~~~
; Mutator
(set-calculator-memory! C 3)

(calculator-memory C)

; I'll re-write memory-plus
(define (memory-plus calc n) 
  (set-calculator-memory! calc (add (calculator-memory calc) n)))

(memory-plus C 2)
(calculator-memory C)

~~~
 
**KRISHNA** Now, `memory-plus` is not referentially transparent any more.  So this 
means that whether its OO or FP, one can make use of mutability or immutability.    

**BRAMHA** Yes, its really about immutability and referential transparency, the 
two properties that allow us to reason out our programs correctly.  I don't think 
its really FP Vs OO.  With OO, the way most languages are, in-place mutation is
a standing invitation.

**BRAMHA** Lets move to the next melody.