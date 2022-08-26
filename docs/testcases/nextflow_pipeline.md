# Pipeline Testing

nf-test also allows to test the complete pipeline end-to-end. Please checkout the [CLI](../cli/generate.md) to generate a pipeline test.

## Syntax
```Groovy
nextflow_pipeline {

    name "<NAME>"
    script "<PATH/TO/NEXTFLOW_SCRIPT.nf>"

    test("<TEST_NAME>") {

    }
}
```
## Assertions

The `workflow` object can be used in asserts to check its status, error messages or traces.

```groovy
// workflow status
assert workflow.success
assert workflow.failed
assert workflow.exitStatus == 0

// workflow error message
assert workflow.errorReport.contains("....")

// trace
//returns a list containing succeeded tasks
assert workflow.trace.succeeded().size() == 3

//returns a list containing failed tasks
assert workflow.trace.failed().size() == 0

//returns a list containing all tasks
assert workflow.trace.tasks().size() == 3
```


## Example

### Nextflow script
Create a new file and name it `pipeline.nf`.

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

workflow {
    input = params.input_text.trim().split(',')
    Channel.from(input) | SAY_HELLO
}
```

### nf-test script
Create a new file and name it `say_hello.nf.test`.

```Groovy
nextflow_pipeline {

    name "Test Process TEST_PROCESS"
    script "pipeline.nf"

    test("Should run without failures") {

        when {
            params {
              input_text = "hello,nf-test"
            }
        }

        then {
        assert workflow.success
        assert workflow.trace.tasks().size() == 2

        }

    }

}
```
