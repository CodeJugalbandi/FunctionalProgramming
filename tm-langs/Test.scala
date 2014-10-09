case class Customer(id: Int, name: String)

class CustomerRepository {
	def findBy(id: Int): Option[Customer] =	{
		if(id > 0) Some(Customer(id, s"Cust #${id}"))
		else None
	}
}

type Request = Map[String, String]

def authorizer(repo: CustomerRepository)(request: Request): Option[Customer] =
  request.get("objectId").flatMap(id => repo.findBy(Integer.parseInt(id)))

def authenticator(f: Request => Option[Customer])(request: Request): Option[Customer] =
  f(request)

val authenticate = authenticator _
val authorize = authorizer _
val request = Map("objectId" -> "1")
val chain = authenticate(authorize(new CustomerRepository))
println(chain(request))

//complexity: O(n)
def last[T](xs: List[T]): T = xs match {
  case Nil => throw new Error("empty.last")
  case List(x) => x
  case first :: rest => last(rest)
}

println(last(List(4 to 9: _*)))

//complexity: O(n)
def init[T](xs: List[T]): List[T] = xs match {
  case Nil => throw new Error("empty.init")
  case List(x) => Nil
  case first :: rest => first :: init(rest) 
}

println(init(List(4 to 9: _*)))

//complexity: O(n)
def concat[T](xs: List[T], ys: List[T]) : List[T] = xs match {
  case Nil => ys
  case first :: rest => first :: concat(rest, ys) 
}

println(concat(List(1 to 3: _*), List(4 to 5: _*)))

//complexity: n * n => quadratic, n for concat and n for reverse
def reverse[T](xs: List[T]): List[T] = xs match {
  case Nil => Nil
  case List(x) => List(x)
  case first :: rest => concat(reverse(rest), List(first))
}

println(reverse(List(4 to 9: _*)))  

//remove nth elem of the list
def remove[T](index: Int, xs: List[T]): List[T] = 
  xs.take(index) ::: xs.drop(index + 1)

println(remove(3, List(4 to 9: _*)))

val s3 = Stream(3, 2, 1, 0 , -1)
println(s3)
println(s3.toList)


def buildStream(n: Int): Stream[Int] = {
  def buildStreamN(acc: Stream[Int], elem: Int): Stream[Int] =
    if (elem < 0) acc else buildStreamN(elem #:: acc, elem - 1)
 
  buildStreamN(Stream.empty, n)
}
  
val s1 = buildStream(5)
println(s1)  

def filter(s: Stream[Int], pred: Int => Boolean): Stream[Int] = 
  if (s.isEmpty) s 
  else if(pred(s.head)) s.head #:: filter(s.tail, pred) 
  else filter(s.tail, pred)

println(filter(s1, x => x % 2 == 0))
val s = Range(1, 100).toStream.map { n => 
       println(s"mapping $n") 
       n * 2 
    }.filter { n =>
       println(s"filtering $n")
       n < 10
    }

println(s.take(4).toList)

// def isort(xs: List[Int]): List[Int] = {
//   def insert(elem: Int, ls: List[Int]): List[Int] = ls match {
//     case Nil => Nil
//     case first :: rest => if (elem < first) elem :: ls else insert(elem, )
//   }
//
//   xs match {
//     case Nil           => Nil
//     case first :: rest => insert(first, rest)
//   }
// }
//
// println(isort(List(4, 3, -2, 9)))