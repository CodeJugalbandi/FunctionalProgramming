package referentialTransparency

case class Calculator(val memory: Int)

object Calculator {
  def add(x: Int, y: Int) = x + y

  def memoryPlus(c: Calculator, n: Int) = c.copy(memory = add(c.memory, n))
}