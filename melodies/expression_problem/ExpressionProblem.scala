case class Circle(r: Int)
case class Rectangle(l: Int, w: Int)

trait ShapeOperations[T] {
  def area(t: T): Double
  def perimeter(t: T): Double
}


implicit object CircleOperations extends ShapeOperations[Circle] {
  def area(c: Circle): Double = Math.PI * c.r * c.r
  def perimeter(c: Circle): Double = 2 * Math.PI * c.r
}

implicit object RectangleOperations extends ShapeOperations[Rectangle] {
  def area(r: Rectangle): Double = r.l * r.w
  def perimeter(r: Rectangle): Double = 2 * (r.l + r.w)
}

def area[T](t: T)(implicit o: ShapeOperations[T]): Double = o.area(t)
def perimeter[T](t: T)(implicit o: ShapeOperations[T]): Double = o.perimeter(t)

val r = Rectangle(2, 3)
area(r)
perimeter(r)


val c = Circle(2)
area(c)
perimeter(c)


trait Graphics[T] {
  def draw(t: T): Unit
}

implicit object CircleGraphics extends Graphics[Circle] {
  def draw(c: Circle) = println(s"Drawing Circle with ${c.r}...")
}

implicit object RectangleGraphics extends Graphics[Rectangle] {
  def draw(r: Rectangle) = println(s"Drawing Rectangle with Length = ${r.l}, Width = ${r.w}...")
}

def draw[T](t: T)(implicit g: Graphics[T]) = g.draw(t)

draw(c)
draw(r)

case class RTriangle(b: Int, h: Int)

implicit object RTriangleOperationsAndGraphics extends ShapeOperations[RTriangle] with Graphics[RTriangle] {
  def area(rt: RTriangle) = 0.5 * rt.b * rt.h
  def perimeter(rt: RTriangle) = rt.b + rt.h + (Math.sqrt(rt.b * rt.b + rt.h * rt.h))
  def draw(rt: RTriangle) = println(s"Drawing RTriangle with Breadth = ${rt.b} and Height = ${rt.h}...")
}

val rt = RTriangle(2, 3)

area(rt)
perimeter(rt)
draw(rt)
