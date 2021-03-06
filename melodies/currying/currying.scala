case class Customer(id: Int, name: String)

class CustomerRepository {
  def findBy(id: Int): Option[Customer] =
  	 if (id > 0)
  	    Some(Customer(id, s"Customer Name # $id"))
  	 else
  	    None
}

type Request = Map[String, Int]

def authenticator(f: Request => Option[Customer])(request: Request): Option[Customer] = 
    f(request)

def authorizer(repository: CustomerRepository)(request: Request): Option[Customer] =
   request.get("id").flatMap(id => repository.findBy(id))
   // request get("id") flatMap repository.findBy

val authorize = authorizer _    
val authenticate = authenticator _ 

val auth = authenticate(authorize(new CustomerRepository))
// Request => Option[Customer] = <function1>

val httpRequest = Map("id" -> 1) 
val cust = auth(httpRequest)
println(cust)