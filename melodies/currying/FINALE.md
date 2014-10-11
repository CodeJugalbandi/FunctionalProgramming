## Partial Function Application
```
(defn new-person [salutation f-name l-name]  {:salutation salutation, :f-name f-name, :l-name l-name})
(def new-mr (partial new-person "Mr"))
(def new-just-joe  #(new-person %1 "Joe" %2))

def salute(salutation: String, fname: String, lname: String) = s"$salutation $fname $lname"
val saluteMr = salute("Mr.", _: String, _: String)
val saluteJoe = salute(_: String, "Joe", _: String)
```
## Currying
```
def salute(salutation: String)(fname: String)(lname: String) = s"$salutation $fname $lname"
salute :: String -> String -> String -> String
```