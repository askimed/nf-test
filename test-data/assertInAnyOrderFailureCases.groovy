/* Test cases for the assertInAnyOrder function in GlobalMethods.java

    Each case mimics the list data structure produced by an nextflow output channel.

    A channel can emit (in any order) a single value or a tuple of values.

    Channels that emit a single item are a list of objects: [a3, a1, a2, ...]
    Channels that emit tuples are list of lists of objects: [[a2,b2], [a1,b1], ...]

    This groovy should throw an exception
*/ 

def myMap1 = [
    'A': [1,2,3],
    'B': [4,5,6]
]

def myMap2 = [
    'A': [1,2,3],
    'B': [4,5,6]
]

def anotherMap1 = [
    'C': [6,6,6],
    'D': [9,9,9]
]

def anotherMap2 = [
    'C': [6,6,6],
    'D': [9,9,9]
]

// Can tell difference with different quantities of multiples
def outputCh = [myMap1, myMap1, anotherMap1, anotherMap1]
def expected = [anotherMap2, anotherMap2, anotherMap2, myMap2]

assertContainsInAnyOrder(outputCh, expected)
