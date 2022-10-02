# Function Testing

nf-test allows to test [functions](https://www.nextflow.io/docs/latest/dsl2.html#function) defined in a Nextflow file or defined in `lib`. Please checkout the [CLI](../cli/generate.md) to generate a function test.

## Syntax
```Groovy
nextflow_function {

    name "<NAME>"
    script "<PATH/TO/NEXTFLOW_SCRIPT.nf>"
    function "<FUNCTION_NAME>"

    test("<TEST_NAME>") {

    }
}
```

### Multiple Functions

If a Nextflow script contains multiple functions and you want to test them all in the same testsuite, you can override the `function` property in each test. For example:

#### `functions.nf`

```Groovy
def function1() {
  ...
}

def function2() {
  ...
}
```

#### `functions.nf.test`

```Groovy
nextflow_function {

    name "Test functions"
    script "functions.nf"

    test("Test function1") {
      function "function1"
      ...
    }

    test("Test function2") {
      function "function2"
      ...
    }
}
```

### Functions in `lib` folder

If you want to test a function that is inside a groovy file in your `lib` folder, you can ignore the `script` property, because Nextflow adds them automatically to the classpath. For example:

#### `lib\Utils.groovy`

```
class Utils {

    public static void sayHello(name) {
        if (name == null) {
            error('Cannot greet a null person')
        }

        def greeting = "Hello ${name}"

        println(greeting)
    }

}
```

#### `tests\lib\Utils.groovy.test`

```Groovy
nextflow_function {

    name "Test Utils.groovy"

    test("Test function1") {
      function "Utils.sayHello"
      ...
    }
}
```

Note: the `generate function` command works only with Nextflow functions.

## Assertions

The `function` object can be used in asserts to check its status, result value or error messages.


```groovy
// function status
assert function.success
assert function.failed

// return value
assert function.result == 27

//returns a list containing all lines from stdout
assert function.stdout.contains("Hello World") == 3
```

## Example

### Nextflow script
Create a new file and name it `functions.nf`.

```Groovy
def say_hello(name) {
    if (name == null) {
        error('Cannot greet a null person')
    }

    def greeting = "Hello ${name}"

    println(greeting)
    return greeting
}
```

### nf-test script
Create a new file and name it `functions.nf.test`.

```Groovy
nextflow_function {

  name "Test Function Say Hello"

  script "functions.nf"
  function "say_hello"

  test("Passing case") {

    when {
      function {
        """
        input[0] = "aaron"
        """
      }
    }

    then {
      assert function.success
      assert function.result == "Hello aaron"
      assert function.stdout.contains("Hello aaron")
      assert function.stderr == []
    }

  }

  test("Failure Case") {

    when {
      function {
        """
        input[0] = null
        """
      }
    }

    then {
      assert function.failed
      //It seems to me that error(..) writes message to stdout
      assert function.stdout.contains("Cannot greet a null person")
    }
  }
}
```

### Execute test

```
nf-test test functions.nf.test
```
