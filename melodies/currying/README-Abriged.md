# Partial Function Application and Currying

**BRAMHA** Imagine, I have an HTTP request for a Customer in our web app and we want to 
authorise the request..let me use Groovy now.
~~~
def httpRequest1 = ['objectId': '1']
def customer = authorizer(customerRepo, httpRequest1)
println customer.name 
~~~

**BRAMHA** Let me write down the Customer repo quickly
~~~
class CustomerRepository {
  def findBy(Integer id) {
     if(id <= 0) {
       throw new RuntimeException('cust not found')
     }
     [id: id, name: "Cust Name #$id"]
  }
}
~~~

**BRAMHA** and I'll write `authorizer` as a closure that consumes 2 parameters, 
repository and request. 
~~~
def authorizer = { CustomerRepository repository, request ->
  def id = request.objectId
  repository.findBy(id)
}
~~~

**BRAMHA** Now lets say that before I can request the customer I need, I need to make 
sure that the user is authorized to access it.  So 
~~~
def custRepo = new CustomerRepository()

def httpRequest1 = ['objectId': '1']
def cust = authorizer(custRepo, httpRequest1)
println cust.name 
 
def httpRequest2 = ['objectId': '2']
cust = authorizer(custRepo, httpRequest2) 
println cust.name
~~~

**BRAMHA** If you look a that above I'm calling authorizer again and again
by injecting custRepo reference.  How about if I can somehow convert this
authorizer to just accept request - the parameter that varies while fixing the
repository?  This is where i'll resort to currying the repository.
~~~
def authorizerWithRepo = authorizer.curry(new CustomerRepository())
def httpRequest1 = ['objectId': '1']
def cust = authorizerWithRepo(httpRequest1)
println cust.name

def httpRequest2 = ['objectId': '2']
cust = authorizerWithRepo(httpRequest2)
println cust.name
~~~

**BRAMHA** So the curried function has already encapsulated the `CustomerRepository`. In 
other words, I've re-shaped the authorizer to a single argument function from a 2-argument
function.  On a similar lines i'll write authenticator that takes in authorizer
as a closure and http request as a parameter and re-shape it to just accept request.
~~~
def authenticator = { Closure authorizeWithRepo, request ->
  authorizeWithRepo(request)
}

def authenticatorWithAuthorizerWithRepo = authenticator.curry(authorizerWithRepo)
//def authenticatorWithAuthorizerWithRepo = authenticator.curry(authorizer.curry(new CustomerRepository())
cust = authenticatorWithAuthorizerWithRepo(httpRequest)
println cust.name
~~~
**BRAMHA** So what we did was used currying to re-shape the functions so that
we can chain them and the request can flow through each of them.

**BRAMHA**  Let me show you some Scala code that does the same thing. 
Bramha then opens up the currying.scala that was pre-written

**BRAMHA**  If you observe the above code, unlike Groovy, I don't invoke any special method 
to do currying, by using multi-parameter functions, Scala gives you currying for free.  All I do here is first convert the methods authorizer and authenticator to function type by partial function application using `_` to give me authorize and authenticate function objects.  Then I inject the repository in authorize and because of currying it then gets re-shaped into a function that goes from `Request => Option[Customer]` which is the exact function type required by authenticator method's first parameter.

**KRISHNA** By passing functions around we already get DI, for free, but by 
currying or by using partially applied function we get contextual DI, because in 
one-place we can inject one thing and at other place we inject another thing. Let's 
look at partial function application in Clojure and start with this function of 3 args:
```
(defn new-person [salutation f-name l-name]
  {:salutation salutation,
   :f-name f-name,
   :l-name l-name})

(new-person "Mr" "Eff" "Pee")
```

**KRISHNA** What if I wanted to partially apply the 'salutation' to this function?
```
(def new-mr (partial new-person "Mr"))
(new-mr "Eff" "Pee")
```

**KRISHNA** I've reshaped the function by wiring in the first arg.  

**KRISHNA** With partial function application, and currying, for that matter, the order of args matter! 
e.g. what if we wanted to fix the second arg instead of the 1st?
~~~
(def new-joe (partial new-person salutation "Joe"))
(new-joe "Mr" "Doe")
~~~
**KRISHNA** But this doesn’t make sense, because we somehow need to have a placeholder for the first 
param, which we’re not wanting to partially apply.  So, when we’re partially applying args to a function, 
it works from left to right. We can’t only apply the 3rd arg and not also the 1st and 2nd arg. 
We can wire in values for the args, from left to right.

**KRISHNA** So we can’t wire in only the 2nd param, but I can always achieve this with a lambda:
~~~
(def new-joe
  (fn [salutation lname]
    (new-person salutation "Joe" lname)))
(new-joe "Mr" "Doe")
~~~

**KRISHNA** Fortunately, Clojure has a macro to synthesise this kind of "order-less" 
partial function application:
```
(def new-just-joe
  #(new-person %1 "Joe" %2))
(new-just-joe "Mrs" "Grow") 
```
**KRISHNA** This "literal function" syntax is even more general than that - we can satisfy 
as many args as we like, and leave the rest. I'm sure Scala has this kind of thing too?

**BRAMHA** Sure, let me show this in Scala. I do partial function application on 
salute by putting in placeholders for its first and last parameters, and just bind
`fname`

```
def salute(salutation: String, fname: String, lname: String) = s"$salutation $fname $lname"
val saluteMr = salute("Mr.", _: String, _: String)
saluteMr("Ryan", "Lemmer") //Mr. Ryan Lemmer
```

**BRAMHA** This time, I do partial function application on salute by putting in placeholders 
for its first and last parameters, and just bind `fname`
```
val saluteJoe = salute(_: String, "Joe", _: String)
saluteJoe("Mr.", "Doe") //Mr. Joe Doe
```
**KRISHNA** Scala's syntax is definitely superior to Clojure's in the sense that regular 
and "orderless" partial function application is unified.

**KRISHNA** Scala's syntax is definitely superior to Clojure's, in the sense that regular and 
"orderless" partial function application is unified, and you get very easy genuine currying.

**KRISHNA** For Currying, on the other hand, let's Haskell:
```
make_person :: String -> String -> String -> String
make_person s fname lname = s ++ fname ++ lname
-- main = putStrLn (make_person "Ms" "Ada" "Lovelace")
-- main = putStrLn ((make_person "Ms") "Ada" "Lovelace")
main = putStrLn (((make_person "Ms") "Ada") "Lovelace")
```

**KRISHNA** In Haskell, your functions are curried by default!

**KRISHNA** In a language that deals well with PFA, you won’t really miss currying, 
because you’ll use PFA where you would have used currying. PFA is practically more powerful 
than currying in some ways. With currying, you have to curry strictly from left to right, 
with PFA you can do orderless application...

**KRISHNA** So should we look at how the currying and PFA in different 
languages look like (finale) :

Let's move on to the next melody!