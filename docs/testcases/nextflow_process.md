# Process Testing

nf-test allows to test each process defined in a module file. Please checkout the [CLI](../cli/generate.md) to generate a process test.

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

The `process` object can be used in asserts to check its status or error messages.


```groovy
// process status
assert process.success
assert process.failed
assert process.exitStatus == 0

// Analyze Nextflow trace file
assert process.trace.tasks().size() == 1

// process error message
assert process.errorReport.contains("....")

//returns a list containing all lines from stdout
assert process.stdout.contains("Hello World") == 3
```

### Output Channels

The `process.out` object provides access to the content of all named output Channels (see Nextflow `emit`):

```groovy
// channel exists
assert process.out.my_channel != null

// channel contains 3 elements
assert process.out.my_channel.size() == 3

// first element is "hello"
assert process.out.my_channel.get(0) == "hello"
```

## Example

### Nextflow script
Create a new file and name it `say_hello.nf`.

```Groovy
#!/usr/bin/env nextflow
nextflow.enable.dsl=2

process SAY_HELLO {
    input:
        val cheers

    output:
        stdout emit: verbiage_ch
        path '*.txt', emit: verbiage_ch2

    script:
    """
    echo -n $cheers
    echo -n $cheers > ${cheers}.txt
    """
}

```

### nf-test script
Create a new file and name it `trial.nf.test`.

```Groovy

nextflow_process {

    name "Test Process SAY_HELLO"
    script "say_hello.nf"
    process "SAY_HELLO"

    test("Should run without failures") {

        when {
            process {
                """
                input[0] = Channel.from('hello','nf-test')
                """
            }
        }

        then {

            assert process.success
            assert process.trace.tasks().size() == 2

            with(process.out.trial_out_ch) {
                assert size() == 2
                assert path(get(0)).readLines().size() == 1
                assert path(get(1)).readLines().size() == 1
                assert path(get(1)).md5 == "4a17df7a54b41a84df492da3f1bab1e3"
            }

        }

    }
}
```

### Execute test
```
nf-test init
nf-test test trial.nf.test
```
