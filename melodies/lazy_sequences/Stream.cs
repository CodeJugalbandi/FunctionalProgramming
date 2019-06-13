using System;
sealed public class Stream<T> {
  public static readonly Stream<T> Empty = new Stream<T>(default(T), null);
  private readonly T head;
  private readonly Lazy<Stream<T>> tail;

  public Stream(T head, Lazy<Stream<T>> tail) {
    this.head = head;
    this.tail = tail;
  }
  public T Head {
    get => head;
  }
  public Stream<T> Tail {
    get => tail.Value;
  }
  public bool IsEmpty {
    get => tail == null;
  }
  public void ForEach(Action<T> action) {
    if (IsEmpty)
      return;
    
    action(Head);
    Tail.ForEach(action);
  }
  // Consing to a stream (Prepend)
  public static Stream<T> operator + (Stream<T> s, T element) => 
    new Stream<T>(element, new Lazy<Stream<T>>(() => s));
  
  // Append
  public static Stream<T> operator + (T element, Stream<T> s) {
    if (s.IsEmpty)
      return new Stream<T>(element, new Lazy<Stream<T>>(() => Stream<T>.Empty));
    
    return new Stream<T>(element, new Lazy<Stream<T>>(() => s.Head + s.Tail));
  }

  public static Stream<R> Of<R>(params R[] rs) {
    var stream = Stream<R>.Empty;
    var indices = rs.Length - 1;
    for (var i = indices; i >= 0; i--) {
      stream = stream + rs[i];
    }
    return stream;
  }
  
  public static Stream<R> Generate<R>(Func<R> fn) => 
    new Stream<R>(fn(), new Lazy<Stream<R>>(() => Generate(fn)));

  public static Stream<R> Iterate<R>(R initial, Func<R, R> fn) => 
    new Stream<R>(initial, new Lazy<Stream<R>>(() => Iterate(fn(initial), fn)));
  
  // Concat another stream  
  public static Stream<T> operator + (Stream<T> @this, Stream<T> other) {
    if (@this.IsEmpty)
      return other;
    
    var concatenated = @this;
    other.ForEach(elem => {
      concatenated = concatenated + elem;
    });
    return concatenated;
  }
  
  // Map  
  public Stream<R> Select<R>(Func<T, R> fn) {
    if (IsEmpty)
      return Stream<R>.Empty;
    
    return new Stream<R>(fn(Head), new Lazy<Stream<R>>(() => Tail.Select(fn)));
  }
  // Filter
  public Stream<T> Where(Predicate<T> pred) {
    if (IsEmpty)
      return Stream<T>.Empty;
    
    if (pred(Head)) 
      return new Stream<T>(Head, new Lazy<Stream<T>>(() => Tail.Where(pred)));
    else 
      return Tail.Where(pred);
  }
  
  public Stream<T> Take(int howMany) {
    if (IsEmpty || howMany <= 0)
      return Stream<T>.Empty;
    
    return new Stream<T>(Head, new Lazy<Stream<T>>(() => Tail.Take(howMany - 1)));
  }
  
  public Stream<T> Drop(int howMany) {
    if (IsEmpty || howMany <= 0)
      return this;
    
    return Tail.Drop(howMany - 1);
  }
  
  // public Stream<R> SelectMany(func<T, Stream<R>> fn) {
  //   if (IsEmpty)
  //     return Stream<R>.Empty;
  //
  //   return new Stream<R>()
  // }
  // public IList<T> ToList() {
  //   if (IsEmpty)
  //     return new List<T>();
  //
  //   var list = new List<T> { Head };
  //   list.Concat(Tail.ToList());
  //   return list;
  // }
  public override string ToString() {
    if (IsEmpty) 
      return "Empty";
    else 
      return $"Stream<{typeof(T)}>({head}, ?)";
  }
}

class Test {
  public static void Main(string[] args) {
    // var empty = new Stream<int>(0, null);
    // var empty = Stream<int>.Empty;
    // Console.WriteLine(empty);      // Empty
    // Console.WriteLine(empty.Head); // Boom
    // Console.WriteLine(empty.Tail); // Boom
    // Console.WriteLine(empty.IsEmpty);
    // Console.WriteLine(empty.ToList());
    // empty.ForEach(Console.WriteLine);
    // Console.WriteLine(empty.Select(x => x * x));
    // Console.WriteLine(empty.Where(x => x < 2));
    
    // var stream = new Stream<int>(2, new Lazy<Stream<int>>(new Stream<int>(1, new Lazy<Stream<int>>(() => empty))));
    // var stream = empty + 1 + 2 + 3 + 4;
    // Console.WriteLine(stream);            // Stream(2, ?)
    // Console.WriteLine(stream.Head);       // 2
    // Console.WriteLine(stream.Tail);       // Stream(1, ?)
    // Console.WriteLine(stream.Tail.Tail);  // Stream(0, ?)
    // Console.WriteLine(stream.Tail.Tail.Tail); // Boom
    // Console.WriteLine(stream.IsEmpty);
    // stream.ForEach(Console.WriteLine);
    // stream.Select(x => x * x).ForEach(Console.WriteLine); // 1 4
    // stream.Where(x => x % 2 == 0).ForEach(Console.WriteLine);  // 1
    // var stream2 = 1 + Stream<int>.Empty;
    // stream2.ForEach(Console.WriteLine); // 1
    
    // var stream2 = 1 + (2 + (3 + (4 + Stream<int>.Empty)));
    // stream2.ForEach(Console.WriteLine); // 1 2 3 4
    
    Stream<int>.Of(1, 2, 3, 4).ForEach(Console.WriteLine);
    // Stream<int>.Of(1, 2, 3, 4).Take(2).ForEach(Console.WriteLine);
    // Stream<int>.Of(1, 2, 3, 4).Take(12).ForEach(Console.WriteLine);
    // Stream<int>.Of(1, 2, 3, 4).Take(0).ForEach(Console.WriteLine);
    // Stream<int>.Of(1, 2, 3, 4).Drop(2).ForEach(Console.WriteLine);
    // Stream<int>.Of(1, 2, 3, 4).Drop(20).ForEach(Console.WriteLine);
    // Stream<int>.Of(1, 2, 3, 4).Drop(0).ForEach(Console.WriteLine);
    Stream<int>.Of<int>().ForEach(Console.WriteLine);
    var random = new Random();
    Stream<int>.Generate(() => random.Next(100, 150)).Take(4).ForEach(Console.WriteLine);
    Stream<int>.Iterate(9, x => x + 2).Take(4).ForEach(Console.WriteLine);
    // var concat1 = Stream<char>.Empty + Stream<char>.Of('a', 'b');
    // concat1.ForEach(Console.WriteLine);
    // var concat2 = Stream<char>.Of('a', 'b') + Stream<char>.Empty;
    // concat2.ForEach(Console.WriteLine);
    // var concat3 = Stream<char>.Of('a', 'b') + Stream<char>.Of('c', 'd', 'e');
    // concat3.ForEach(Console.WriteLine);
    
  }    
}