trait Bool extends Function2[Object, Object, Object] {
  def unary_! = this(False, True)
  def &&(other: Bool) = this(other(True, False), False)
  def ||(other: Bool) = this(True, other(True, False))
  def ⊕ (other: Bool) = this(other(False, True), other(True, False))
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
println("DONE")
