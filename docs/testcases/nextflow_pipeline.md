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

```Groovy
nextflow_pipeline {

    name "Test Workflow test1.nf"
    script "test-data/test1.nf"

    test("Should run without failures") {

        when {
            params {
                outdir = "tests/results"
            }
        }

        then {
            assert workflow.success
            assert path("tests/results/output.txt").exists()
        }

    }

}

```
