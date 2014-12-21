class Stream<T> {
  public static final Stream<T> Empty = new Stream(null, { null }) 
  final T _head
  final Closure _tail
  
  Stream(head, Closure tail) {
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
  def leftShift(element) {
    new Stream(element, {this})
  }
  
  static def from(n) {
    new Stream(n, { from(n + 1) })
  }
  def take(howMany) {
    if (isEmpty() || howMany <= 0) Stream.Empty
    else new Stream(head(), {tail().take(howMany - 1)})
  }
  def force() {
    if(isEmpty()) []
    else [head()] + tail().force()
  }
  def filter(Closure predicate) {
    if(isEmpty()) Stream.Empty
    else if(predicate(head()))
      new Stream(head(), { tail().filter(predicate) })
    else
      tail().filter(predicate)
  }
}

def sieve(s) {
  def first = s.head()
  def rest = s.tail().filter { it % first != 0 }
  new Stream(first, {sieve(rest)})
}

println sieve(Stream.from(2)).take(10).force()

