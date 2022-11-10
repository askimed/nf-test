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

Channels that emit a single item appear as a list of objects, eg: `process.out.outputCh = [a3, a1, a2, ...]`
Channels that emit tuples appear as a list of lists which contain objects, eg: `process.out.outputCh = [[a2,b2], [a1,b1], ...]`

To perform agnostic assertions on channels, `nf-test` provides: `assertInAnyOrder(List<object> list1, List<object> list2)`

Some example use-cases are provided below.

### Channel that emits strings
```groovy
def process.out.outputCh = ['Hola', 'Hello', 'Bonjour']

def expected = ['Bonjour', 'Hello', 'Hola']
assertInAnyOrder(process.out.outputCh, expected)

```

### Channel that emits a single maps, e.g. val(myMap)
```groovy
def process.out.outputCh = [
  [
    'A': [1,2,3],
    'B': [4,5,6]
  ],
  [
    'C': [7,8,9],
    'D': [10,11,12]
  ],
]

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

See: PathExtensions for more information on parsing and asserting various file types.

Since the outputCh filepaths are different between consecutive runs, the files need to be read/parsed prior to comparison

```groovy
def process.out.outputCh = ['/path/to/some/file1.json', '/path/to/another/file2.json']

def actual = process.out.outputCh.collect { filepath -> path(filepath).json }
def expected = [path('./myTestData/file2.json').json, path('./myTestData/file1.json').json]

assertInAnyOrder(actual, expected)

```

### Channel that emits a tuple of strings and json files

See: PathExtensions for more information on parsing and asserting various file types

Since ordering of the items within the tuples are consistent, we can assert this case:

```groovy
def process.out.outputCh = [['Hello', '/path/to/some/file1.json'], ['Hola', '/path/to/another/file2.json']]

def actual = process.out.outputCh.collect { greeting, filepath -> [greeting, path(filepath).json] }
def expected = [
  ['Hola', path('./myTestData/file2.json').json], 
  ['Hello', path('./myTestData/file1.json').json]
]

assertInAnyOrder(actual, expected)
```

If you only wanted to assert the json, and ignore the string:
```groovy
def process.out.outputCh = [['Hello', '/path/to/some/file1.json'], ['Hola', '/path/to/another/file2.json']]

def actual = process.out.outputCh.collect { greeting, filepath -> path(filepath).json }
def expected = [
  path('./myTestData/file2.json').json, 
  path('./myTestData/file1.json').json
]

assertInAnyOrder(actual, expected)
```

If you only wanted to assert the strings and not the json files:
```groovy
def process.out.outputCh = [['Hello', '/path/to/some/file1.json'], ['Hola', '/path/to/another/file2.json']]

def actual = process.out.outputCh.collect { greeting, filepath -> greeting }
def expected = ['Hola', 'Hello']

assertInAnyOrder(actual, expected)
```

If you only wanted to assert a single json file that occurs in one of the tuples:
```groovy
def process.out.outputCh = [['Hello', '/path/to/some/file1.json'], ['Hola', '/path/to/another/file2.json']]

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