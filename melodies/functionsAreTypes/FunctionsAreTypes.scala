trait Bool {
  def choose(x: Object, y: Object): Object
  def unary_! = choose(False, True)
  def &&(other: Bool) = choose(other.choose(True, False), False)
  def ||(other: Bool) = choose(True, other.choose(True, False))
  def ⊕ (other: Bool) = choose(other.choose(False, True), other.choose(True, False))
}

object True extends Bool {
  def choose(x: Object, y: Object) = x
  override def toString() = "true"
} 

object False extends Bool {
  def choose(x: Object, y: Object) = y
  override def toString() = "false"
} 

println(True.choose(new Integer(10), new Integer(20)))
println(False.choose(new Integer(10), new Integer(20)))

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
