def square(x: Int) = x * x

square(2)


//Function as Value (Entity), I can pass around cube as parameters
//to other functions just like normal values
val cube = (x: Int) => x * x * x

cube(2)

Range(0, 25).toList.map(cube)

Range(0, 25).toList.map(square)
