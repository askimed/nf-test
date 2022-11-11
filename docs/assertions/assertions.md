# Assertions

Writing test cases means formulating assumptions by using assertions. Groovy’s power assert provides a detailed output when the boolean expression validates to false. nf-test provides several extensions and commands to simplify the work with Nextflow channels.

## Using `with`

This assertions...

```groovy
assert process.out.imputed_plink2
assert process.out.imputed_plink2.size() == 1
assert process.out.imputed_plink2.get(0).get(0) == "example.vcf"
assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen"
assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"
```

... can be written by using `with(){}` to improve readability:

```groovy
assert process.out.imputed_plink2
with(process.out.imputed_plink2) {
    assert size() == 1
    with(get(0)) {
        assert get(0) == "example.vcf"
        assert get(1) ==~ ".*/example.vcf.pgen"
        assert get(2) ==~ ".*/example.vcf.psam"
        assert get(3) ==~ ".*/example.vcf.pvar"
    }
}
```

## Comparing Channels with Using `assertInAnyOrder`

Nextflow channels can emit (in any order) a single value or a tuple of values. 

Channels that emit a single item produce as an unordered list of objects, 
`List<Object>`, for example:
```groovy
process.out.outputCh = ['Bonjour', 'Hello', 'Hola']
```

Channels that contain Nextflow `file` values have a unique path each run. For Example
```groovy
process.out.outputCh = ['Bonjour', 'Hello', 'Hola']
```
 
Channels that emit tuples produce an unordered list of ordered object lists, `List<List<Object>>`
```groovy
process.out.outputCh = [[a2,b2], [a1,b1], ...]
```


`assertInAnyOrder(List<object> list1, List<object> list2)` performs an order agnostic assertion on channels and lists contents. The method is automatically imported into every `nf-test` closure. It is a binding for Hamcrest's [assertInAnyOrder](http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matchers.html#containsInAnyOrder(org.hamcrest.Matcher))

Some example use-cases are provided below.

Note: `nf-test` attempts to pre-sort the Channel through integer, string and path comparisons. This makes repeatability comparisons by index possible, but can fail to produce repeatable orderings when the data contains other class types. A warning message appears if the channel contains other classes objects. An alternative is to handle assertions using the method outlined here.


### Channel that emits strings
```groovy
// process.out.outputCh = ['Hola', 'Hello', 'Bonjour']

def expected = ['Bonjour', 'Hello', 'Hola']
assertInAnyOrder(process.out.outputCh, expected)

```

### Channel that emits a single maps, e.g. val(myMap)
```groovy
/*
process.out.outputCh = [
  [
    'A': [1,2,3],
    'B': [4,5,6]
  ],
  [
    'C': [7,8,9],
    'D': [10,11,12]
  ],
]
*/

def expected = [
  [
    'D': [10,11,12],
    'C': [7,8,9]
  ],
  [
    'B': [4,5,6],
    'A': [1,2,3]
  ]
]
assertInAnyOrder(process.out.outputCh, expected)

```

### Channel that emits json files

See [the files page](./files.md) for more information on parsing and asserting various file types.

Since the outputCh filepaths are different between consecutive runs, the files need to be read/parsed prior to comparison

```groovy
/*
process.out.outputCh = [
  '/path/to/some/file1.json', 
  '/path/to/another/file2.json'
]
*/

def actual = process.out.outputCh.collect { filepath -> path(filepath).json }
def expected = [
  path('./myTestData/file2.json').json, 
  path('./myTestData/file1.json').json
]

assertInAnyOrder(actual, expected)

```

### Channel that emits a tuple of strings and json files

See [the files page](./files.md) for more information on parsing and asserting various file types

Since the ordering of items within the tuples are consistent, we can assert this case:

```groovy
/*
process.out.outputCh = [
  ['Hello', '/path/to/some/file1.json'], 
  ['Hola', '/path/to/another/file2.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> [greeting, path(filepath).json] }
def expected = [
  ['Hola', path('./myTestData/file2.json').json], 
  ['Hello', path('./myTestData/file1.json').json]
]

assertInAnyOrder(actual, expected)
```

To assert the json only and ignore the strings:
```groovy
/*
process.out.outputCh = [
  ['Hello', '/path/to/some/file1.json'], 
  ['Hola', '/path/to/another/file2.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> path(filepath).json }
def expected = [
  path('./myTestData/file2.json').json, 
  path('./myTestData/file1.json').json
]

assertInAnyOrder(actual, expected)
```

To assert the strings only and not the json files:
```groovy
/*
process.out.outputCh = [
  ['Hello', '/path/to/some/file1.json'], 
  ['Hola', '/path/to/another/file2.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> greeting }
def expected = ['Hola', 'Hello']

assertInAnyOrder(actual, expected)
```

To assert that one of json files occurs in one of the tuples:
```groovy
/*
def process.out.outputCh = [
  ['Hello', '/path/to/some/file1.json'], 
  ['Hola', '/path/to/another/file2.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> path(filepath).json }

assert actual.contains(path('./myTestData/file2.json').json)
```


## Using `assertAll`
`assertAll(Closure... closures)` ensures that all supplied closures do no throw exceptions. The number of failed closures is reported in the Exception message. This useful for efficient debugging
a set of test assertions in one go.

```groovy
def a = 2

assertAll(
	{ assert a==1 },
	{ a = 1/0 },
	{ assert a==2 },
	{ assert a==3 }
)
```
The output will look like this:
```groovy

assert a==1
       ||
       |false
       2

java.lang.ArithmeticException: Division by zero
Assertion failed:

assert a==3
       ||
       |false
       2

FAILED (7.106s)

  java.lang.Exception: 3 of 4 assertions failed
```
