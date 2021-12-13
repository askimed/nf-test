# Workflow Testing
nf-test also allows to test a specific workflow. Please checkout the [CLI](../cli/generate.md) to generate a workflow test.
## Syntax
```Groovy
nextflow_workflow {

    name "<NAME>"
    script "<PATH/TO/NEXTFLOW_SCRIPT.nf>"
    workflow "<WORKFLOW_NAME>"

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

//returns a list containing all lines from stdout
assert workflow.stdout.contains("Hello World") == 3

```


## Example

```Groovy
nextflow_workflow {

    name "Test Workflow TEST_WORKFLOW"
    script "test-data/test_workflow.nf"
    workflow "TEST_WORKFLOW"

    test("Should run without failures") {

        when {
            params {
                outdir = "tests/results"
            }
            workflow {
                """
                input[0] = file("test-file.txt")
                """
            }
        }

        then {
            assert workflow.success
            assert path("tests/results/output.txt").exists()
        }

    }

}
```
