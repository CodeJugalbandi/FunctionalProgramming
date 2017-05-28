# type from functions

## Bool: type or function?
**BRAMHA** I'd like to explore the line between code and type. Lets choose a boolean type.  Why are booleans needed?  To make choices.  We at least need two things to make a choice from. Using functions to represent True and False boolean values.  Let's play with it in Scala:

```
trait Bool extends Function2[Object, Object, Object] {
}

object True extends Bool {
  def apply(x: Object, y: Object) = x
  override def toString() = "true"
} 

object False extends Bool {
  def apply(x: Object, y: Object) = y
  override def toString() = "false"
} 

println(True(new Integer(10), new Integer(20)))
println(False(new Integer(10), new Integer(20)))

```

**BRAMHA** Lets add a few boolean operations like ```not```, ```and``` and ```or```

```
trait Bool extends Function2[Object, Object, Object] {
  def unary_! = this(False, True)
  def &&(other: Bool) = this(other(True, False), False)
  def ||(other: Bool) = this(True, other(True, False))
  def ⊕ (other: Bool) = this(other(False, True), other(True, False))
}

println("Negation...")
println(!True)
println(!False)

println("ANDing...")
println(True && False)
println(False && True)
println(True && False)
println(True && True)

println("ORing...");
println(False || False)
println(False || True)
println(True || False)
println(True || True)

println("XORing...");
println(False ⊕ False);
println(False ⊕ True);
println(True ⊕ False);
println(True ⊕ True);

```

