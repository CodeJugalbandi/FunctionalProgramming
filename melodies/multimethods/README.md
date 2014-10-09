**BRAMHA** Lets say we have a Rocket that takes in an engine to fly...I'll use Java this time

```
class Rocket {
  void fly(Engine e) {
    System.out.println("Rocket Flying with " + e);
  }
}

class Engine {
  public String toString() { return "Engine"; }
}
```

**BRAMHA**  I now need a moon rocket and its not enough to have plain engines....
```
class LOHEngine extends Engine {
  public String toString() { return "LOHEngine"; }
}


class MoonRocket extends Rocket {
  void fly(LOHEngine e) {
    System.out.println("MoonRocket Flying with " + e);
  }
}
```
**BRAMHA** Let me write a class to test the rockets out.  If you see, though I would
have expected MoonRocket to fly with LOHEngine, but it does not and instead uses the
Engine instead, I'll never reach to moon with that...
```
class Test {
  public static void main(String[] args) {
    Rocket r = new Rocket();
    r.fly(new Engine());

    Rocket mr = new MoonRocket();
    mr.fly(new LOHEngine());
  }
}
```
**BRAMHA** However, if I copy-paste the same code in Groovy, because all valid Java
code is Groovy code, I observe that I'd be able to reach moon now, aiye...

```
class Engine {
  String toString() { "Engine" }
}

class LOHEngine extends Engine {
  String toString() { "LOHEngine" }
}

class Rocket {
  def fly(Engine e) {
    println "Rocket Flying with $e"
  }
}

class MoonRocket extends Rocket {
  def fly(LOHEngine e) {
    println "MoonRocket Flying with $e"
  }
}

Rocket r = new Rocket()
r.fly(new Engine())

Rocket mr = new MoonRocket()
mr.fly(new LOHEngine())
```
**BRAMHA**  Whats going on here is that multi-methods are at work...In Java/C#, the
dispatch is based on the type, whereas in Groovy the dispatch is not only
based on the type of the receiver but also on the type of the parameters
of the method.  How does it look in Clojure?

**KRISHNA** Let me show you this in action in Clojure...  (Ryan: Can we have the
same rocket example coded here in Clojure so people can relate to the things?)
```
(def debit-journal [:debit 25 "1 Oct 2014" "invoice"])
(def credit-journal [:credit 26 "3 Oct 2014" "payment"])

(defmulti save-j (fn [j] (first j)))

(defmethod save-j :debit [j]
  (println "DEBIT: " j))
(defmethod save-j :credit [j]
  (println "CREDIT: " j))

(save-j debit-journal)
(save-j credit-journal)
```