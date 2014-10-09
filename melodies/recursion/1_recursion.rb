# RUBY... sum an array of nums - iterate over array assign to an accumulator
# Iterative Sum
def sum_iter(arr)
  acc = 0
  arr.each {|num| acc += num}
  acc
end

sum_iter([2 3 5])
# acc = 0
# acc += 2  ->  2
# acc += 3  ->  5
# acc += 5  ->  10

# acc is CHANGED IN PLACE (mutation)
# acc has a LIFECYCLE (on a timeline)


# ====================================
# how do we make a SUM
# that does not use VARIALBES?

# sum_rec([2 3 5])
# 2 + sum_rec([3 5])
# 2 + (3 + sum_rec([5]))
# 2 + (3 + (5 + sum_rec[]))
# 2 + (3 + (5 + 0))        -> only primitives!
# 2 + (3 + 5)
# 2 + (8)
# 10

# ====================================
# Recursive SUM
def sum2(arr)
  if arr.empty?
    0
  else
    (first arr) + sum2(rest arr)
  end
end

# Recursive Process
# sum_rec([2 3 5])

first it EXPANDS!
  -> builds chain of deferred operations
# 2 + sum_rec([3 5])
# 2 + (3 + sum_rec([5]))
# 2 + (3 + (5 + sum_rec[]))
# 2 + (3 + (5 + 0)) -> only primitives!

then it starts to FOLD back in
# 2 + (3 + 5)
# 2 + (8)
# 10

# folds back from the deepest inner nest
# back outwards


# ====================================
# sum_rec([2 3 5])
# 2 + sum_rec([3 5])
# 2 + (3 + sum_rec([5]))
# 2 + (3 + (5 + sum_rec[]))
# 2 + (3 + (5 + 0))        -> only primitives!
# 2 + (3 + 5)
# 2 + (8)
# 10

# * use of SPACE grows linearly with 'arr' size
# * LINEAR recursion


def sum2(arr)
  if arr.empty?
    0
  else
    (first arr) + sum2(rest arr)
  end
end
# WHERE is 'acc' ?!?!

# HIDDEN info is being maintained by the runtime
# (that doesn't appear in any program vars)
# ====================================

