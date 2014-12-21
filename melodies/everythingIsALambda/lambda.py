
def square(x):
    return x * x
    
print square(2)    # 4
print type(square) # <type 'function'>

squareLambda = lambda x: x * x

print squareLambda(2) # 4
print type(squareLambda) # <type 'function'>

list = range(5)
print list  # [0, 1, 2, 3, 4]
print map(squareLambda, list) # [0, 1, 4, 9, 16]


