# Assertions

Writing test cases means formulating assumptions by using assertions. Groovy’s power assert provides a detailed output when the boolean expression validates to false. nf-test provides several extensions and commands to simplify the work with Nextflow channels. Here we summarise how nextflow and nf-test handles channels and provide examples for the tools that `nf-test` provides: 

- `with`: assert the contents of an item in a channel by index
- `contains`: assert the contents of an item in the channel is present anywhere in the channel
- `assertContainsInAnyOrder`: order-agnostic assertion of the contents of a channel

## Nextflow channels and nf-test channel sorting

Nextflow channels emit (in a random order) a single value or a tuple of values. 

Channels that emit a single item produce an unordered list of objects, `List<Object>`, for example:
```groovy
process.out.outputCh = ['Hola', 'Hello', 'Bonjour']
```

Channels that contain Nextflow `file` values have a unique path each run. For Example:
```groovy
process.out.outputCh = ['/.nf-test/tests/c563c/work/65/85d0/Hola.json', '/.nf-test/tests/c563c/work/65/fa20/Hello.json', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json']
```
 
Channels that emit tuples produce an unordered list of ordered objects, `List<List<Object>>`:
```groovy
process.out.outputCh = [
  ['Hola', '/.nf-test/tests/c563c/work/65/85d0/Hola.json'], 
  ['Hello', '/.nf-test/tests/c563c/work/65/fa20/Hello.json'], 
  ['Bonjour', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json']
]
```

Assertions by channel index are made possible through sorting of the nextflow channel. The sorting is performed automatically by `nf-test` prior to launch of the `then` closure via integer, string and path comparisons. For example, the above would be sorted by `nf-test`:
```groovy
process.out.outputCh = [
  ['Bonjour', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json'],
  ['Hello', '/.nf-test/tests/c563c/work/65/fa20/Hello.json'],
  ['Hola', '/.nf-test/tests/c563c/work/65/85d0/Hola.json']
]
``` 

`nf-test` cannot guarantee the order of nextflow channels that contain alternative object types, such as maps. A warning message appears in the console in such cases to highlight the potential issue. To test these channels, the `contains` and `assertContainsInAnyOrder` methods described below can be used
```
Warning: Cannot sort channel, order not deterministic. Unsupported objects types:
```

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

## Using `contains` to assert an item in the channel is present

Groovy's [contains](https://docs.groovy-lang.org/latest/html/groovy-jdk/java/lang/Iterable.html#contains(java.lang.Object)) and [collect](https://docs.groovy-lang.org/latest/html/groovy-jdk/java/lang/Iterable.html#collect()) methods can be used to flexibly assert an item exists in the channel output. 

For example, the below represents a channel that emits a two-element tuple, a string and a json file: 
```groovy
/*
def process.out.outputCh = [
  ['Bonjour', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json'],
  ['Hello', '/.nf-test/tests/c563c/work/65/fa20/Hello.json'],
  ['Hola', '/.nf-test/tests/c563c/work/65/85d0/Hola.json']
]
*/
```

To assert the channel contains one of the tuples, parse the json and assert:
```groovy
testData = process.out.outputCh.collect { greeting, jsonPath -> [greeting, path(jsonPath).json] } 
assert testData.contains(['Hello', path('./myTestData/Hello.json').json])
``` 

To assert a subset of the tuple data, filter the channel using collect. For example, to assert the greeting only:
```groovy
testData = process.out.outputCh.collect { greeting, jsonPath -> greeting } 
assert testData.contains('Hello')
```

See [the files page](./files.md) for more information on parsing and asserting various file types.


## Using `assertContainsInAnyOrder` for order-agnostic assertion of the contents of a channel

`assertContainsInAnyOrder(List<object> list1, List<object> list2)` performs an order agnostic assertion on channels contents and is available in every `nf-test` closure. It is a binding for Hamcrest's [assertContainsInAnyOrder](http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matchers.html#containsInAnyOrder(org.hamcrest.Matcher)).

Some example use-cases are provided below.

### Channel that emits strings
```groovy
// process.out.outputCh = ['Bonjour', 'Hello', 'Hola'] 

def expected = ['Hola', 'Hello', 'Bonjour']
assertContainsInAnyOrder(process.out.outputCh, expected)

```

### Channel that emits a single maps, e.g. val(myMap)
```groovy
/*
process.out.outputCh = [
  [
    'D': [10,11,12],
    'C': [7,8,9]
  ],
  [
    'B': [4,5,6],
    'A': [1,2,3]
  ]
]
*/

def expected = [
  [
    'A': [1,2,3],
    'B': [4,5,6]
  ],
  [
    'C': [7,8,9],
    'D': [10,11,12]
  ]
]

assertContainsInAnyOrder(process.out.outputCh, expected)

```


### Channel that emits json files

See [the files page](./files.md) for more information on parsing and asserting various file types.

Since the outputCh filepaths are different between consecutive runs, the files need to be read/parsed prior to comparison

```groovy
/*
process.out.outputCh = [
  '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json',
  '/.nf-test/tests/c563c/work/65/fa20/Hello.json',
  '/.nf-test/tests/c563c/work/65/85d0/Hola.json'
]
*/

def actual = process.out.outputCh.collect { filepath -> path(filepath).json }
def expected = [
  path('./myTestData/Hello.json').json,
  path('./myTestData/Hola.json').json,
  path('./myTestData/Bonjour.json').json,
]

assertContainsInAnyOrder(actual, expected)

```

### Channel that emits a tuple of strings and json files

See [the files page](./files.md) for more information on parsing and asserting various file types.

Since the ordering of items within the tuples are consistent, we can assert this case:

```groovy
/*
process.out.outputCh = [
  ['Bonjour', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json'],
  ['Hello', '/.nf-test/tests/c563c/work/65/fa20/Hello.json'],
  ['Hola', '/.nf-test/tests/c563c/work/65/85d0/Hola.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> [greeting, path(filepath).json] }
def expected = [
  ['Hola', path('./myTestData/Hola.json').json], 
  ['Hello', path('./myTestData/Hello.json').json],
  ['Bonjour', path('./myTestData/Bonjour.json').json],
]

assertContainsInAnyOrder(actual, expected)
```

To assert the json only and ignore the strings:
```groovy
/*
process.out.outputCh = [
  ['Bonjour', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json'],
  ['Hello', '/.nf-test/tests/c563c/work/65/fa20/Hello.json'],
  ['Hola', '/.nf-test/tests/c563c/work/65/85d0/Hola.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> path(filepath).json }
def expected = [
  path('./myTestData/Hello.json').json, 
  path('./myTestData/Hola.json').json,
  path('./myTestData/Bonjour.json').json
]

assertContainsInAnyOrder(actual, expected)
```

To assert the strings only and not the json files:
```groovy
/*
process.out.outputCh = [
  ['Bonjour', '/.nf-test/tests/c563c/work/65/b62f/Bonjour.json'],
  ['Hello', '/.nf-test/tests/c563c/work/65/fa20/Hello.json'],
  ['Hola', '/.nf-test/tests/c563c/work/65/85d0/Hola.json']
]
*/

def actual = process.out.outputCh.collect { greeting, filepath -> greeting }
def expected = ['Hello', 'Hola', 'Bonjour]

assertContainsInAnyOrder(actual, expected)
```


## Using `assertAll`
`assertAll(Closure... closures)` ensures that all supplied closures do no throw exceptions. The number of failed closures is reported in the Exception message. This useful for efficient debugging
of a set of test assertions from a single test run.

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
