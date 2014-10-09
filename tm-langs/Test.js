function toSingletonSet(element) {
  return function(item) {
    return item === element;
  }
}

var singletonSet = toSingletonSet(2)

singletonSet(2) //true

singletonSet(3) //false

function contains(set, item) {
  return set(item);
}

contains(singletonSet, 2) //true
contains(singletonSet, 3) //false

// Combining 2 sets produces a set
function union(s1, s2) {
  return function(element) {
     return contains(s1, element) || contains(s2, element);
  }
}

var unionSet = union(toSingletonSet(3), union(toSingletonSet(4), toSingletonSet(5)))
unionSet(3) //true
unionSet(4) //true
unionSet(5) //true
unionSet(6) //false

//diff: return all elements of s1 that are not in s2
function difference(s1, s2) {
  return function(elem) {
     return contains(s1, elem) && !contains(s2, elem);
  }
}

var diffSet = difference(union(toSingletonSet(3), toSingletonSet(4)), toSingletonSet(3));
diffSet(4) //true
diffSet(3) //false


//filter
function filter(s, predF) {
  return function(elem) {
    return contains(s, elem) && predF(elem);
  }
}

var filteredSet = filter(unionSet, function(elem) { return elem % 2 !== 0; });
console.log(contains(filteredSet, 3)); //true
console.log(contains(filteredSet, 4)); //false
console.log(contains(filteredSet, 5)); //true
console.log(contains(filteredSet, 6)); //false
