import groovy.transform.*

class Stream<T> {
  public static final Stream<T> Empty = new Stream(null, {-> null }) 
  final T _head
  final Closure _tail
  
  private Stream(head, Closure tail) {
    _head = head
/*    _tail = tail*/
    _tail = tail.memoize()
  }
  def isEmpty() { _tail() == null }
  def head() {
    if(isEmpty())
      throw new Exception('Head of Empty Stream!')
    return _head
  }
  def tail() {
    if(isEmpty())
      throw new Exception('Tail of Empty Stream!')
    return _tail()
  }
  String toString() {
    if(isEmpty()) '[]'
    else "[${head()}, ?]"
  }
  //append element to stream
  def leftShift(T element) {
    new Stream(element, {this})
  }
  //concat with other
  def leftShift(Stream<?> other) {
    if (isEmpty()) other
    else new Stream(head(), {-> tail() << other})
  }  
  @TailRecursive def fold(Stream<T> stream = this, acc, Closure fn) {
    if(stream.isEmpty()) acc
    else fold(stream.tail(), fn(acc, stream.head()), fn)
  }     
  @TailRecursive def foldN(Stream<T> stream = this, howMany, acc, Closure fn) {
    if(stream.isEmpty() || howMany <= 0) acc
    else foldN(stream.tail(), howMany - 1, fn(acc, stream.head()), fn)
  }   
  def reverse() {
    fold(Stream.Empty) { acc, elem -> acc << elem }
  }
  def call(index) {
    def list = take(index + 1).force()
    if (index > list.size() - 1) 
      throw new Exception("Index $index out of bounds") 
    
    list[index]
  }
  static def from(n) {
    iterate(n) { it + 1 }
  } 
  static def iterate(initial, Closure fn) {
    new Stream(initial, {-> iterate(fn(initial), fn) })
  }  
  static def of(T ...ts) {
    def stream = Stream.Empty
    ts.reverseEach { 
      stream = stream << it
    }
    stream
  }
  static def generate(Closure fn) {
    new Stream(fn(), {-> generate(fn)})
  }
  static def range(Range range, int step = 1) {
    Stream.of(*range.step(step))
  }
  def drop(howMany) {
    foldN(howMany, this) { stream, elem -> stream.tail() }
  }
  def take(howMany) {
    if (isEmpty() || howMany <= 0) Stream.Empty
    else new Stream(head(), {-> tail().take(howMany - 1)})
  }
  def takeWhile(Closure predicate) {
    if (isEmpty()) 
      Stream.Empty
    else if (predicate(head())) 
      new Stream(head(), {-> tail().takeWhile(predicate)})
    else 
      Stream.Empty
  }
  def dropWhile(Closure predicate) {
    if (isEmpty()) 
      Stream.Empty
    else if (predicate(head())) 
      tail().dropWhile(predicate)
    else 
      this
  }
  def force() {
    fold([]) { acc, elem -> acc << elem }
  }
  def filter(Closure predicate) {
    if(isEmpty()) Stream.Empty
    else if(predicate(head()))
      new Stream(head(), {-> tail().filter(predicate) })
    else
      tail().filter(predicate)
  } 
  def map(Closure fn) {
    if (isEmpty()) Stream.Empty
    else new Stream(fn(head()), {-> tail().map(fn) })
  } 
  def flatMap(Closure fn) {
    if (isEmpty()) Stream.Empty
    else fn(head()) << tail().flatMap(fn)
  }
  def collect(Closure predicate) {
    if (isEmpty()) 
      Stream.Empty
    else if (predicate(head()))
      new Stream(predicate(head()), {-> tail().collect(predicate)})
    else
      tail().collect(predicate)
  } 
  def each(Closure fn) {
    if (isEmpty()) 
      Stream.Empty
    else {
      fn(head())
      tail().each(fn)
    }
  }
  def zip(Stream<?> that) {
    if (isEmpty() || that.isEmpty()) 
      Stream.Empty
    else
      new Stream(new Tuple(head(), that.head()), {-> tail().zip(that.tail()) })
  } 
  def zipWithIndex() {
    zip(from(0))
  }
  def split(Closure predicate) {
    fold([Stream.Empty, Stream.Empty]) { streams, elem -> 
       def (yays, nays) = streams
       if (predicate(elem)) [yays << elem, nays]
       else [yays, nays << elem] 
    }  
  }
}

def empty = Stream.Empty
println empty.takeWhile { it == 1 }
println empty.dropWhile { it == 1 }

/*def stream = empty << 2 << 1 << 2 << 1 << 1 << 1*/
def stream = Stream.of(1, 1, 1, 2, 1, 2)
println "Stream = ${stream}"
println "Stream Force = ${stream.force()}"
println "Stream Take 3 force = ${stream.take(3).force()}"
println "Stream Map = ${stream.map { it * 2 }.force()}"
println "Stream Filter = ${stream.filter { it % 2 == 0 }.force()}"
println "Stream Reversed = ${stream.reverse().force()}"
println "Stream TakeWhile = ${(stream.takeWhile { it == 1 }.force())}"
println "Stream DropWhile = ${(stream.dropWhile { it == 1 }.force())}"
println "Stream Fold = ${(stream.fold(0) { acc, elem -> acc + elem })}"
println "Stream FoldN(2) = ${(stream.foldN(2, 0) { acc, elem -> acc + elem })}"
def squaredEvens = stream.collect { 
   if (it % 2 == 0) it * it // Square Evens, leave Odds
   else null  // Groovy truth expression that evaluates to false
}        
println "Stream collect squaredEvens = ${squaredEvens.force()}" 
stream.each { print it }
println ""

def s1 = Stream.of(2, 1, 0)
def s2 = Stream.of(6, 5, 4)
def zipped = s1.zip(s2)
println "Zipped Stream = ${zipped.force()}"  // [2, 6, 1, 5, 0, 4]
println "Collated Zipped Stream = ${zipped.force().collate(2)}" // [[2, 6], [1, 5], [0,4]]

println "Sum of 2 streams ="
println zipped.map {
  def (first, second) = it
  first + second
}.force()   // [8, 6, 4]
println "Zip with Index = "
stream.zipWithIndex().each {
  print it  
}

def (evens, odds) = stream.split { it % 2 == 0 }
println "After Split Odds = ${odds.force()}"
println "After Split Evens = ${evens.force()}"


def sieve(s) {
  def first = s.head()
  def rest = s.tail().filter { it % first != 0 }
  new Stream(first, {sieve(rest)})
}

println sieve(Stream.from(2)).take(10).force()

def fibonacci(s) {
  def next = s.zip(s.tail()).map {
    def (first, second) = it
    first + second
  }
  def rest = s << next.head()
  new Stream(s.head(), {-> fibonacci(rest)})
}

def s = Stream.from(0).take(2)
println s.force() // [0, 1]
// Start with seed elements 0 and 1
println fibonacci(s).take(10).force()
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
// 
Stream xs = Stream.of(1, 2)
Stream cs = Stream.of('a', 'b')

println ((xs << cs).force())
println xs.flatMap { i -> cs }.force()

def n = 10
println Stream.from(1).take(n).flatMap { i ->
  Stream.from(1).take(n).flatMap { j ->
    Stream.from(1).take(n).map { k ->
        [i, j, k]
    }.filter { triplet ->
      def (x, y, z) = triplet
      x*x + y*y == z*z
    }
  }
}.force()

println xs.flatMap { x -> 
           cs.map { c ->
             [c, x]
           } 
         }.force()

println "Naturals = " + Stream.iterate(0) { it + 1 }.take(3).force()
println "Fibonacci = " + Stream.iterate([0, 1]) { 
  def (first, second) = it
  [second, first + second]
}.map{ 
  def (first, second) = it
  first
}.take(10).force()

def rnd = new java.util.Random()
println "Generating random stream = " + Stream.generate{ rnd.nextInt(10) }.take(10).force()
println Stream.range('a'..'z').force()
println Stream.range('A'..'Z', 2).force()
println Stream.range(-10..5, 4).force()
println Stream.range(new Date("12-Dec-2012")..new Date("19-Jan-2013"), 8)
