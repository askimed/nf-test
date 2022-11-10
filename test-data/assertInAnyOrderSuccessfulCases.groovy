/* Test cases for the assertInAnyOrder function in GlobalMethods.java

    Each case mimics the list data structure produced by an nextflow output channel.

    A channel can emit (in any order) a single value or a tuple of values.

    Channels that emit a single item are a list of objects: [a3, a1, a2, ...]
    Channels that emit tuples are list of lists of objects: [[a2,b2], [a1,b1], ...]

    The test cases here ensure json files, maps and sublists can be compared in an order-agnostic manner

    This groovy should run and not throw anything
*/ 

def myMap = [
    'A': [1,2,3],
    'B': [4,5,6]
]

def anotherMap = [
    'C': [6,6,6],
    'D': [9,9,9]
]

def outputCh = null
def expected = null
def jsonPath = './test-data/example.json'
def channelTestData = null

/* A channel that emits a single Map */
outputCh = [myMap]

expected = [
    [
        'B': [4,5,6],
        'A': [1,2,3]
    ]
]

assertContainsInAnyOrder(outputCh, expected)

/* Channel emits two maps out of order  */
outputCh = [myMap, anotherMap]

expected = [anotherMap, myMap]

assertContainsInAnyOrder(outputCh, expected)

/* Channel emits a single Json file */
outputCh = [jsonPath]

expected = [path(jsonPath).json]
channelTestData = outputCh.collect { file -> path(file).json }

assertContainsInAnyOrder(channelTestData, expected)

/* Channel emits a single Map/Json Tuple */
outputCh = [[myMap, jsonPath]]

expected = [[myMap, path(jsonPath).json]]
channelTestData = outputCh.collect { map, file -> [map, path(file).json] }

assertContainsInAnyOrder(channelTestData, expected)

/* Channel emits two Map/Json Tuples out of order */
outputCh = [[myMap, jsonPath], [anotherMap, jsonPath]]

// Assert whole channel
channelTestData = outputCh.collect { map, file -> [map, path(file).json] }
expected = [[anotherMap, path(jsonPath).json], [myMap, path(jsonPath).json]]

assertContainsInAnyOrder(channelTestData, expected)

// Assert the Json only
outputCh = [[myMap, jsonPath], [anotherMap, jsonPath]]

channelTestData = outputCh.collect { map, file -> path(jsonPath).json }
expected = [path(jsonPath).json, path(jsonPath).json]

assertContainsInAnyOrder(channelTestData, expected)

// Assert the channel contains one of the maps in one of the emitted elements
// TODO: Put in the docs
outputCh = [[myMap, jsonPath], [anotherMap, jsonPath]]

channelTestData = outputCh.collect { map, file -> map }
expected = anotherMap

assert channelTestData.contains(expected)

