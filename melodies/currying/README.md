# Partial Function Application and Currying

**BRAMHA** Imagine, I have an HTTP request for a Customer in our web app and we want to 
authorise the request..let me use Groovy now.

```
def httpRequest1 = ['id': 1]

def customer = authorizer(customerRepo, httpRequest1)
println customer.name 
```

**BRAMHA** Let me write down the Customer repo quickly

```
class CustomerRepository {
  def findBy(Integer id) {
     if(id > 0) {
       [id: id, name: "Cust Name #$id"]
     } else {
       throw new RuntimeException('cust not found')
     }
  }
}
```

**BRAMHA** and I'll write `authorizer` as a closure that consumes 2 parameters, 
repository and request. 

```
def authorizer = { CustomerRepository repository, request ->
  def id = request.id
  repository.findBy(id)
}
```

**BRAMHA** Now lets say that before I can request the customer, I need to make sure that the user is authorized to access it:

```
def custRepo = new CustomerRepository()

def httpRequest1 = ['id': '1']
def httpRequest2 = ['id': 2]

def cust = authorizer(custRepo, httpRequest1)

def cust2 = authorizer(custRepo, httpRequest2) 
```

**BRAMHA** 
If you look at the above I'm repeatedly injecting custRepo reference when calling authorizer.  
Could I somehow convert this authorizer to just accept request - the parameter that varies 
while fixing the repository? This is where i'll resort to currying the repository.

~~~
def auth = authorizer.curry(custRepo)

def cust = auth(httpRequest1)
def cust2 = auth(httpRequest2)
~~~

**BRAMHA** I've re-shaped the authorizer to a single argument function from a 2-argument function.  Along similar lines I'll write an authenticator that takes in an authorizer and http-request and re-shape it to just accept request: 

~~~
def authenticator = { repo, req ->
  repo(req)
}

def auth = authenticator.curry(authorizer.curry(custRepo))
cust = auth(httpRequest)
~~~

**BRAMHA** We used currying to re-shape the functions so that we can chain them and the request can flow through each of them.

Let me show you some Scala code that does the same thing. 

~~~
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

  val authorize = authorizer _    
  val authenticate = authenticator _ 

  val auth = authenticate(authorize(new CustomerRepository))
  // Request => Option[Customer] = <function1>
  
  val httpRequest = Map("id" -> 1) 
  val cust = auth(httpRequest)
~~~

**BRAMHA** If you observe the above code, unlike Groovy, I don't invoke any special method to do currying, by using multi-parameter functions, Scala gives you currying for free.

All I do here is convert the methods authorizer and authenticator to function type by partial function application using _ to give me authorize and authenticate function objects. 

Then I inject the repository in authorize and because of currying it then gets re-shaped into a function that goes from `Request => Option[Customer]`.  

Notice, because of currying the signature of the authenticate is `(Request => Option[Customer]) => (Request => Option[Customer])` and after injecting the authorize into authenticate, the signature of authenticateWithAuthorizeWithRepo function becomes `Request => Option[Customer]`

**KRISHNA** This shows the difference between Currying and PFA. Currying refers to functions that take only one arg at a time! Whereas with PFA, we are just dealing with regular functions of multiple args. You can think of a Curried function as a nest of functions: each function takes one arg, and returning a function that takes the next arg, and so on, until all the args are exhausted.

**KRISHNA** Let's look at partial function application in Clojure and start with this function of 3 args:

```
(defn new-person [title f-name l-name]
  {:title title,
   :f-name f-name,
   :l-name l-name})

(new-person "Ms" "Ada" "Lovelace")
```

**KRISHNA** What if I wanted to partially apply the `title` to this function?

```
(def new-ms (partial new-person "Ms"))
(new-ms "Ada" "Lovelace")
```

**KRISHNA** I've reshaped the function by wiring in the first arg.  

**KRISHNA** With partial function application, and currying, for that matter, the order of args matter! e.g. what if we wanted to fix the second arg instead of the 1st?

~~~
(def new-ada (partial new-person title "Ada"))
(new-ada "Ms" "Lovelace")
~~~

**KRISHNA** But this doesn’t make sense, because we somehow need to have a placeholder for the first param, which we’re not wanting to partially apply.  So, when we’re partially applying args to a function, it works from left to right. We can’t only apply the 3rd arg and not also the 1st and 2nd arg. We can wire in values for the args, from left to right.

We can’t wire in only the 2nd param, but I can always achieve this with a lambda:

~~~
(def new-ada
  (fn [title lname]
    (new-person title "Ada" lname)))
(new-ada "Ms" "Lovelace")
~~~

**KRISHNA** Fortunately, Clojure has a macro to synthesise this kind of "ad-hoc" 
partial function application:

```
(def new-ada-f
  #(new-person %1 "Ada" %2))

(new-ada-f "Ms" "Lovelace")
```

**KRISHNA** I'm sure Scala has this kind of thing too?

**BRAMHA** Sure, let me show this in Scala. I do partial function application on 
new_person by putting in placeholders for its 2nd and 3rd parameters, and just bind
`title`

```
def new_person(title: String, fname: String, lname: String) 
  = s"$title $fname $lname"
  
val new_ms = new_person("Ms", _: String, _: String)  

new_ms("Ada", "Lovelace")
```
**BRAMHA** This time, I do partial function application on fname by putting in placeholders for its first and last parameters:

```
val new_ada = new_person(_: String, "Ada", _: String)

new_ada("Ms", "Lovelace")
```
**KRISHNA** Scala's syntax is definitely superior to Clojure's in the sense that regular and ad-hoc partial function application is unified.

**BRAMHA** Also, in Scala, if you have a function in curried form, that is, a function of one arg at a time, it's trivial to convert it to the uncurried form, and vice versa:

```
val fUncurried = Function.uncurried(f)
val fCurried = fUncurried.curried
```

**KRISHNA** In Haskell, functions are curried by default:

```
make_person :: String -> String -> String -> String
make_person s fname lname = s ++ fname ++ lname
-- main = putStrLn (make_person "Ms" "Ada" "Lovelace")
-- main = putStrLn ((make_person "Ms") "Ada" "Lovelace")
main = putStrLn (((make_person "Ms") "Ada") "Lovelace")
```

**KRISHNA**  In a language that deals well with PFA, you won’t really miss currying, because you’ll use PFA where you would have used currying. 

Also, "ad-hoc PFA" is practically more powerful than currying in some ways. With currying, you have to curry strictly from left to right, with PFA you can do ad-hoc application of args.
