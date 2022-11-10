import static groovy.test.GroovyAssert.*


// assert workflow.out.trial_out_ch == ["hello", "nf-test", "HYPE"]
// assert workflow.out.trial_out_ch[3] == "hello"

// println "workflow.out.trial_out_ch"
// println workflow.out.trial_out_ch

// assertListUnsorted(workflow.out.trial_out_ch, ["hello", "nf-test", "HYPE"])

def myMap = [:]
def anotherMap = [:]
def channel = []
def expected = []
def jsonPath = '/home/a.fishman/repos/nf-test/test-data/process/path-util/input.json'

myMap = [
    'A': [1,2,3],
    'B': [4,5,6]
]

/* A channel that emits a single Map */
channel = [myMap]

expected = [
    [
        'A': [1,2,3],
        'B': [4,5,6]
    ]
]

assertContainsInAnyOrder(channel, expected)

/* Channel emits two maps out of order  */
anotherMap = [
    'C': [6,6,6],
    'D': [9,9,9]
]

channel = [myMap, anotherMap]
expected = [anotherMap, myMap]

assertContainsInAnyOrder(channel, expected)

/* Channel emits a single Json file */
channel = [jsonPath]

expected = [path(jsonPath).json]

println "--------JSON-------"
println path(jsonPath).json

channel = channel.collect { file -> path(file).json }
assertContainsInAnyOrder(channel, expected)

/* Channel emits a single Map/Json Tuple */
channel = [[myMap, jsonPath]]
expected = [[myMap, path(jsonPath).json]]

channel = channel.collect { map, file -> [map, path(file).json] }
assertContainsInAnyOrder(channel, expected)

/* Channel emits two Map/Json Tuples out of order */
channel = [[myMap, jsonPath], [anotherMap, jsonPath]]

// Assert whole channel
channel = channel.collect { map, file -> [map, path(file).json] }
expected = [[anotherMap, path(jsonPath).json], [myMap, path(jsonPath).json]]
assertContainsInAnyOrder(channel, expected)

// Assert the Json only
channel = [[myMap, jsonPath], [anotherMap, jsonPath]]

def channelJson = channel.collect { map, file -> path(jsonPath).json }
expected = [path(jsonPath).json, path(jsonPath).json]
assertContainsInAnyOrder(channelJson, expected)

// Assert just one of the maps
channel = [[myMap, jsonPath], [anotherMap, jsonPath]]

def channelMaps = channel.collect { map, file -> map }
expected = anotherMap

assert channelMaps.contains(expected)

