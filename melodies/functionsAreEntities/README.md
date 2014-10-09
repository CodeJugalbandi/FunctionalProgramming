# data from functions

## a list: data or function?
**BRAMHA** I'd like to explore the line between code and data. Let's play with a regular list in Clojure:

```
(first '(1 2 3))
> 1

(first (rest '(1 2 3)))
> 2

(first (rest (rest '(1 2 3))))
> 3
```

**BRAMHA** The list is a data structure. And if we want to get at a value in the structure, we can only do so with *first* and *rest*. So, we can represent the "list data" any way we like, as long as we can use *first* and *rest* to get at the data!

```
(defn my-cons [_elem _list]
  (fn [selector]
    (cond (= selector 'first)
            _elem
          (= selector 'rest)
            _list)))
          
((my-cons 1 []) 'first)
> 1

(def nums
  (my-cons 1
           (my-cons 2
                    (my-cons 3 []))))          
```
**BRAMHA** the list, *nums*, is a lambda, but we can still use *first* and *rest* to extract the data from the list:

```
(nums 'first)
> 1

((nums 'rest) 'first)
> 2

(((nums 'rest) 'rest) 'first)
> 3
```


## Set: data or function?
**KRISHNA** We've constructed data from functions! Let me see, what would the analogy be for a Set? We'd need to construct a set, test for membership, and so on. I'm going to use Javascript for this…

```
function toSingletonSet(element) {
  return function(item) {
    return item === element;
  }
}
var singletonSet = toSingletonSet(2)

singletonSet(2) 
> true

singletonSet(3) 
> false

function contains(set, item) {
  return set(item);
}

contains(singletonSet, 2) 
> true

contains(singletonSet, 3) 
> false
```
**KRISHNA** In this sense, a set is just a lambda that tests for membership! That means the union of 2 sets is also just a lambda that tests for membership:

```
function union(s1, s2) {
  return function(element) {
     return contains(s1, element) || contains(s2, element);
  }
}

var unionSet = union(toSingletonSet(3), 
                     union(toSingletonSet(4), toSingletonSet(5)))

unionSet(3)
> true

unionSet(4)
> true

unionSet(5) 
> true

unionSet(6) 
> false
```
**KRISHNA** the same goes for set *difference*, and even set *filter*:

```
function difference(s1, s2) {
  return function(elem) {
     return contains(s1, elem) && !contains(s2, elem);
  }
}

var diffSet = difference(union(toSingletonSet(3), toSingletonSet(4)), 
                         toSingletonSet(3));
diffSet(4)
> true

diffSet(3) 
>false

function filter(s, predF) {
  return function(elem) {
    return contains(s, elem) && predF(elem);
  }
}

var filteredSet = filter(unionSet, 
                         function(elem) { return elem % 2 !== 0; });

contains(filteredSet, 3) 
>true

contains(filteredSet, 4) 
> false

contains(filteredSet, 5) 
>true

contains(filteredSet, 6) 
>false
```
**BRAHMA** nice! what about *map*?

**KRISHNA** I was hoping you wouldn't ask that! *map* is a bit weird because a mapped set would have to check whether the pre-mapped value is part of the set. So you would need the inverse of the map function to test for membership of the pre-mapped value. 

**BRAHMA** I see, let's leave it at that. Besides, this implementation is not very practical; it would take much more memory space than a more concrete data representation.

**KRISHNA** So, in answer to your original question of whether a list/set is data or function, my answer is, "that's an implementation detail".
