 val ages = List(34, 32, 33, 23, 21, 19 ,5, 47, 50)

case class Person(age: Int)

def notTeens(p: Person) = p.age > 20
def maxAge(p1: Person, p2: Person) = if(p1.age > p2.age) p1 else p2
def minAge(p1: Person, p2: Person) = if(p1.age < p2.age) p1 else p2

println(ages map Person filter notTeens reduce minAge age)
println(ages map Person filter notTeens reduce maxAge age)
println(ages map Person filterNot notTeens reduce minAge age)
println(ages map Person filterNot notTeens reduce maxAge age)

