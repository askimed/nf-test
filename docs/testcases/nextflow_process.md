# Process Testing

## Syntax
```Groovy
nextflow_process {

    name "<NAME>"
    script "<PATH/TO/NEXTFLOW_SCRIPT.nf>"
    process "<PROCESS_NAME>"

    test("<TEST_NAME>") {

    }
}
```

## Assertions

### Process

The `process` object can be used in asserts to check its status or error messages.


```groovy
// process status
assert process.success
assert process.failed
assert process.exitStatus == 0

// process error message
assert process.errorReport.contains("....")
```

### Output Channels

The `process.out` object provides access to the content of all named output Channels (see Nextflow `emit`):

```groovy
// channel exists
assert process.out.my_channel != null

// channel contains 3 elements
assert process.out.my_channel.size() == 4

// first element is "hello"
assert process.out.my_channel.get(0) == "hello"
```

## Example

```Groovy
nextflow_process {

    name "Test Process TEST_PROCESS"
    script "test-data/test_process.nf"
    process "TEST_PROCESS"

    test("Should run without failures") {

        when {
            params {
                outdir = "tests/results"
            }
            process {
                """
                input[0] = file("test-file.txt")
                """
            }
        }

        then {
            assert process.success
            assert process.out.out_channel != null
        }

    }

}
```
