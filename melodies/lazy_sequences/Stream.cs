using System;
using System.Collections.Generic;
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
  
  // Concat another stream  
  public static Stream<T> operator + (Stream<T> @this, Stream<T> other) {
    if (@this.IsEmpty)
      return other;
    
    return new Stream<T>(@this.Head, new Lazy<Stream<T>>(() => @this.Tail + other));
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
  
  // Deconstruction
  public void Deconstruct(out T first, out Stream<T> rest) {
    if (IsEmpty) 
      throw new ArgumentException("Collection is Empty!");
    
    first = Head;
    rest = Drop(1);
  }
  
  public void Deconstruct(out T first, out T second, out Stream<T> rest) =>
    (first, (second, rest)) = this;
    
  public void Deconstruct(out T first, out T second, out T third, out Stream<T> rest) =>
    (first, second, (third, rest)) = this;
  
  
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
  // Flatmap
  public Stream<R> SelectMany<R>(Func<T, Stream<R>> fn) {
    if (IsEmpty)
      return Stream<R>.Empty;

    return fn(Head) + Tail.SelectMany(fn);
  }
  
  public Stream<T> Reverse() {
    Stream<T> Reverse0(Stream<T> acc, Stream<T> source) {
      if (source.IsEmpty)
        return acc;
      
      return Reverse0(acc + source.Head, source.Tail);
    }
    
    return Reverse0(Stream<T>.Empty, this);
  }
  
  public Stream<T> TakeWhile(Predicate<T> pred) {
    if (IsEmpty)
      return Stream<T>.Empty;
    
    if (pred(Head))
      return Head + Tail.TakeWhile(pred);
    
    return Stream<T>.Empty;
  }
  
  public Stream<T> DropWhile(Predicate<T> pred) {
    if (IsEmpty)
      return Stream<T>.Empty;
    
    if (pred(Head))
      return Tail.DropWhile(pred);
    
    return this;
  }
  
  public Stream<(T,U)> Zip<U>(Stream<U> that) {
    if (this.IsEmpty || that.IsEmpty)
      return Stream<(T,U)>.Empty;
    
    return (this.Head, that.Head) + this.Tail.Zip(that.Tail);
  }
  
  public Stream<R> ZipWith<U, R>(Stream<U> that, Func<T, U, R> fn) {
    if (this.IsEmpty || that.IsEmpty)
      return Stream<R>.Empty;
    
    return fn(this.Head, that.Head) + this.Tail.ZipWith(that.Tail, fn);
  }
  // Aggregate or FoldLeft/Reduce
  public U Aggregate<U>(U identity, Func<U, T, U> func) {
    if (IsEmpty)
      return identity;
    
    return Tail.Aggregate(func(identity, Head), func);
  }
  
  public bool All(Predicate<T> pred) {
    bool All0(bool accumulator, Stream<T> stream) {
      if (stream.IsEmpty || accumulator == false)
        return accumulator;

      return All0(accumulator && pred(stream.Head), stream.Tail);
    }
    return All0(true, this);
  }
  
  public bool Any(Predicate<T> pred) {
    bool Any0(bool accumulator, Stream<T> stream) {
      if (stream.IsEmpty || accumulator == true)
        return accumulator;

      return Any0(accumulator || pred(stream.Head), stream.Tail);
    }
    return Any0(false, this);
  }
  
  // Scan
  public Stream<U> Scan<U>(U identity, Func<U, T, U> func) {
    if (IsEmpty)
      return Stream<U>.Empty;

    U newHead = func(identity, Head);
    return newHead + Tail.Scan(newHead, func);
  }

  public (Stream<T>, Stream<T>) Split(Predicate<T> pred) {
    (Stream<T>, Stream<T>) Split0(Stream<T> yesAcc, Stream<T> noAcc, Stream<T> source) {
      if (source.IsEmpty) 
        return (yesAcc.Reverse(), noAcc.Reverse());
      
      var elem = source.Head;
      if (pred(elem))
        return Split0(yesAcc + elem, noAcc, source.Tail);
      else
        return Split0(yesAcc, noAcc + elem, source.Tail);
    }
    
    return Split0(Stream<T>.Empty, Stream<T>.Empty, this);
  }
  
  public List<T> ToList() {
    var result = new List<T>();
    ForEach(result.Add);
    return result;
  }
  
  public override string ToString() {
    if (IsEmpty) 
      return "Empty";
    return $"Stream<{typeof(T)}>({head}, ?)";
  }
}

class Test {
  public static Stream<int> Sieve(Stream<int> s) {
    var first = s.Head;
    var rest = s.Tail.Where(n => n % first != 0);
    return new Stream<int>(first, new Lazy<Stream<int>>(() => rest));
  }
  
  public static Stream<int> From(int start) => Stream<int>.Iterate(start, x => x + 1);
    
  public static Stream<int> Fibonacci(Stream<int> s) {
    var next = s.Zip(s.Tail).Select(tuple => {
      var (first, second) = tuple;
      return first + second;
    });
    return new Stream<int>(s.Head, new Lazy<Stream<int>>(() => Fibonacci(s + next.Head)));
  }
  
  public static void Main(string[] args) {
    // var empty = new Stream<int>(0, null);
    var empty = Stream<int>.Empty;
    Console.WriteLine(empty);      // Empty
    // Console.WriteLine(empty.Head); // Boom
    // Console.WriteLine(empty.Tail); // Boom
    Console.WriteLine(empty.IsEmpty);
    empty.ForEach(Console.WriteLine);

    var stream = new Stream<int>(2, new Lazy<Stream<int>>(new Stream<int>(1, new Lazy<Stream<int>>(() => empty))));
    Console.WriteLine(stream);            // Stream(2, ?)
    Console.WriteLine(stream.Head);       // 2
    Console.WriteLine(stream.Tail);       // Stream(1, ?)
    Console.WriteLine(stream.Tail.Tail);  // Stream(0, ?)
    // Console.WriteLine(stream.Tail.Tail.Tail); // Boom
    Console.WriteLine(stream.IsEmpty);
    stream.ForEach(Console.WriteLine);

    // Prepend Operator +
    var stream1 = empty + 1 + 2;
    Console.WriteLine(stream1);            // Stream(2, ?)
    Console.WriteLine(stream1.Head);       // 2
    Console.WriteLine(stream1.Tail);       // Stream(1, ?)
    Console.WriteLine(stream1.Tail.Tail);  // Stream(0, ?)

    // Append Operator +
    // var stream2 = 1 + Stream<int>.Empty;
    // stream2.ForEach(Console.WriteLine); // 1
    var stream2 = 1 + (2 + (3 + (4 + Stream<int>.Empty)));
    stream2.ForEach(Console.WriteLine); // 1 2 3 4

    // Create Stream using - Of
    Stream<int>.Of(1, 2, 3, 4).ForEach(Console.WriteLine);
    Stream<int>.Of<int>().ForEach(Console.WriteLine);         // Prints Nothing

    // Concat Operator +
    var concat1 = Stream<char>.Empty + Stream<char>.Of('a', 'b');
    concat1.ForEach(Console.Write); // ab
    Console.WriteLine();
    var concat2 = Stream<char>.Of('a', 'b') + Stream<char>.Empty;
    concat2.ForEach(Console.Write); // ab
    Console.WriteLine();
    var concat3 = Stream<char>.Of('a', 'b') + Stream<char>.Of('c', 'd', 'e');
    concat3.ForEach(Console.Write); // abcde
    Console.WriteLine();

    // Take
    Stream<int>.Empty.Take(2).ForEach(Console.WriteLine);           // Prints Nothing
    Stream<int>.Of(1, 2, 3, 4).Take(2).ForEach(Console.WriteLine);  // 1 2
    Stream<int>.Of(1, 2, 3, 4).Take(12).ForEach(Console.WriteLine); // 1 2 3 4
    Stream<int>.Of(1, 2, 3, 4).Take(0).ForEach(Console.WriteLine);  // Prints Nothing

    // Drop
    Stream<int>.Empty.Drop(2).ForEach(Console.WriteLine);           // Prints Nothing
    Stream<int>.Of(1, 2, 3, 4).Drop(2).ForEach(Console.WriteLine);  // 3 4
    Stream<int>.Of(1, 2, 3, 4).Drop(20).ForEach(Console.WriteLine); // Prints Nothing
    Stream<int>.Of(1, 2, 3, 4).Drop(0).ForEach(Console.WriteLine);  // 1 2 3 4

    // Generate
    var random = new Random();
    Stream<int>.Generate(() => random.Next(100, 150)).Take(4).ForEach(Console.WriteLine);

    // Iterate
    Stream<int>.Iterate(9, x => x + 2).Take(4).ForEach(Console.WriteLine); // 9 11 13 15

    // Transform each element.
    Console.WriteLine(empty.Select(x => x * x));
    stream.Select(x => x * x).ForEach(Console.WriteLine); // 1 4

    // Select using Predicate
    stream.Where(x => x % 2 == 0).ForEach(Console.WriteLine);  // 1
    Console.WriteLine(empty.Where(x => x < 2));

    // SelectMany (flatMap)
    Stream<char>.Of('a', 'b')
      .SelectMany(c => Stream<int>.Of(1, 2).Select(n => (c, n)))
      .ForEach(t => Console.Write(t));  // (a, 1)(a, 2)(b, 1)(b, 2)
    Console.WriteLine();

    Stream<int>.Empty.SelectMany(c => Stream<int>.Of(1, 2).Select(n => (c, n)))
      .ForEach(t => Console.Write(t));  // Prints Nothing
    Console.WriteLine();

    // Reverse
    Stream<char>.Of('a', 'b', 'c').Reverse().ForEach(Console.Write); // cba
    Console.WriteLine();
    Stream<int>.Empty.Reverse().ForEach(Console.WriteLine); // Prints Nothing

    // TakeWhile
    Stream<char>.Of('a', 'a', 'b', 'c')
      .TakeWhile(c => c == 'a')
      .ForEach(Console.Write); // aa
    Console.WriteLine();

    Stream<char>.Of('a', 'a', 'b', 'c')
      .TakeWhile(c => c == 'b')
      .ForEach(Console.Write); // Prints Nothing
    Console.WriteLine();

    // DropWhile
    Stream<char>.Of('a', 'a', 'b', 'c')
      .DropWhile(c => c == 'a')
      .ForEach(Console.Write); // bc
    Console.WriteLine();

    Stream<char>.Of('a', 'a', 'b', 'c')
      .DropWhile(c => c == 'b')
      .ForEach(Console.Write); // aabc
    Console.WriteLine();

    // Zip
    Stream<char>.Of('a', 'b').Zip(Stream<int>.Of(1, 2))
      .ForEach(t => Console.Write(t)); // (a, 1)(b, 2)
    Console.WriteLine();

    Stream<char>.Of('a', 'b').Zip(Stream<int>.Empty)
      .ForEach(t => Console.Write(t)); // Prints Nothing
    Console.WriteLine();

    Stream<int>.Empty.Zip(Stream<char>.Of('a', 'b'))
      .ForEach(t => Console.Write(t)); // Prints Nothing
    Console.WriteLine();

    // ZipWith
    var numbers = Stream<int>.Of(1, 2, 3);
    numbers.ZipWith(numbers, (n1, n2) => n1 * n2)
      .ForEach(Console.WriteLine); // 1 4 9
    Console.WriteLine();

    numbers.ZipWith(Stream<int>.Empty, (n1, n2) => n1 * n2)
      .ForEach(Console.WriteLine); // Prints Nothing
    Console.WriteLine();

    Stream<int>.Empty.ZipWith(numbers, (n1, n2) => n1 * n2)
      .ForEach(Console.WriteLine); // Prints Nothing
    Console.WriteLine();

    // Split
    var (evens, odds) = Stream<int>.Iterate(0, x => x + 1).Take(10).Split(x => x % 2 == 0);
    evens.ForEach(Console.Write); // 02468
    Console.WriteLine();
    odds.ForEach(Console.Write);  // 13579
    Console.WriteLine();

    // ToList
    var list = Stream<int>.Iterate(1, x => x + 1).Take(4).ToList();
    foreach (var item in list) {
      Console.WriteLine(item);
    }

    // Sieve
    Console.WriteLine("Sieve...");
    Sieve(From(2)).Take(6).ForEach(Console.Write);
    Console.WriteLine();

    // Fibonacci
    Console.WriteLine("Fibonacci...");
    var seed = From(0).Take(2);
    // Start with seed elements 0 and 1
    Fibonacci(seed)
      .Take(6)
      .ForEach(Console.WriteLine);

    Console.WriteLine("Fibonacci...using Iterate");
    Stream<int>.Iterate((0, 1), tuple => {
      var (first, second) = tuple;
      var next = first + second;
      return (second, next);
    })
    .Select(tuple => {
      var (fst, snd) = tuple;
      return fst;
    })
    .Take(6)
    .ForEach(Console.WriteLine);

    var sentence = "All...";
    var indexed = Stream<int>.Iterate(0, x => x + 1)
      .Take(sentence.Length)
      .Select(n => (sentence[n], n))
      .ToList();

    foreach (var item in indexed) {
      Console.WriteLine(item);
    }

    // Aggregate
    var sum1 = Stream<int>.Of(1, 2, 3, 4).Aggregate(0, (acc, elem) => acc + elem);
    Console.WriteLine($"sum = {sum1}"); // 10
    var sum2 = Stream<int>.Of<int>().Aggregate(0, (acc, elem) => acc + elem);
    Console.WriteLine($"sum = {sum2}"); // 0

    // All
    Console.WriteLine(Stream<int>.Of<int>().All(x => x % 2 == 0));        // True
    Console.WriteLine(Stream<int>.Of<int>(2, 4).All(x => x % 2 == 0));    // True
    Console.WriteLine(Stream<int>.Of<int>(1, 2, 4).All(x => x % 2 == 0)); // False
    
    // Any
    Console.WriteLine(Stream<int>.Of<int>().Any(x => x % 2 == 0));        // False
    Console.WriteLine(Stream<int>.Of<int>(2, 4).Any(x => x % 2 == 0));    // True
    Console.WriteLine(Stream<int>.Of<int>(1, 2, 4).Any(x => x % 2 == 0)); // True
    Console.WriteLine(Stream<int>.Of<int>(1, 3).Any(x => x % 2 == 0));    // False

    // Scan (prints running sum)
    Stream<int>.Of(1, 2, 3, 4).Scan(0, (acc, elem) => acc + elem).ForEach(Console.WriteLine);
    // 1 3 6 10
    Stream<int>.Of<int>().Scan(0, (acc, elem) => acc + elem).ForEach(Console.WriteLine);  // Prints Nothing

   
    // // Deconstruction
    // var (head, rest) = Stream<int>.Of(1, 2, 3, 4);
    // Console.WriteLine("Head = " + head); // 1
    // Console.WriteLine("Rest = " + rest); // Stream(2, ?)
    
    // // Deconstruction
    // var (head, second, rest) = Stream<int>.Of(1, 2, 3, 4);
    // Console.WriteLine("Head = "   + head);   // 1
    // Console.WriteLine("Second = " + second); // 2
    // Console.WriteLine("Rest = "   + rest);   // Stream(2, ?)
    
    // Deconstruction
    var (head, _second, third, rest) = Stream<int>.Of(1, 2, 3, 4);
    Console.WriteLine("Head = "   + head);   // 1
    Console.WriteLine("Second = " + _second); // 2
    Console.WriteLine("Third = "  + third);  // 3
    Console.WriteLine("Rest = "   + rest);   // Stream(4, ?)
    
    Console.WriteLine("Done!");
  }    
}