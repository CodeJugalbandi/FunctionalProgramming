def iterate(acc, list) {
  if (list.isEmpty())
         acc
  else
         iterate(acc + list.head(), list.tail())
}

def sum(list) {
  iterate(0, list)
}

list = 1..5000
println (sum(list))
