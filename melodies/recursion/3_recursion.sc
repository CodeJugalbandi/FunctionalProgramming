package recursion

object _3_recursion {
  val list = List(1, 2, 3, 4)                     //> list  : List[Int] = List(1, 2, 3, 4)
  
  //Iteration - in place mutation of total
  def sum(xs : List[Int]): Int = {
    var total = 0
  	for (x <- xs) {
  		total += x
  	}
  	total
  }                                               //> sum: (xs: List[Int])Int
  sum(list)                                       //> res0: Int = 10
  
  //Sum - Recursive
  def sumR(xs: List[Int]): Int = {
  	if (xs.isEmpty) 0 else xs.head + sumR(xs.tail)
  }                                               //> sumR: (xs: List[Int])Int
  sumR(list)                                      //> res1: Int = 10
  
  // Sum - Using Recursion but Iterative without inplace mutation
  def sumIR(xs: List[Int]): Int = {
  	def iterate(acc: Int, ys: List[Int]): Int = {
  		if (ys.isEmpty) acc else iterate(acc + ys.head, ys.tail)
  	}
  	iterate(0, xs)
  }                                               //> sumIR: (xs: List[Int])Int
  
  sumIR(list)                                     //> res2: Int = 10
  
  //count items in list using iteration
  
  def countIR(xs: List[Int]): Int = {
  	def iterate(acc: Int, ys: List[Int]): Int = {
  		if (ys.isEmpty) acc else iterate(acc + 1, ys.tail)
  	}
  	iterate(0, xs)
  }                                               //> countIR: (xs: List[Int])Int
  
  countIR(list)                                   //> res3: Int = 4
  
  
  def iterate(xs: List[Int], acc: Int, fn : Int => Int): Int = {
  	if(xs.isEmpty) acc else iterate(xs.tail, acc + fn(xs.head), fn)
  }                                               //> iterate: (xs: List[Int], acc: Int, fn: Int => Int)Int
  
  def sumIR2(xs : List[Int]): Int = iterate(xs, 0, x => x)
                                                  //> sumIR2: (xs: List[Int])Int
  
  sumIR2(list)                                    //> res4: Int = 10
  
  def countIR2(xs: List[Int]): Int = iterate(xs, 0, x => 1)
                                                  //> countIR2: (xs: List[Int])Int
  
  countIR2(list)                                  //> res5: Int = 4
  
}