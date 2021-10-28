# `nextflow_workflow`

## Properties

### `name`
### `script`
### `workflow`


## Example

```Groovy
nextflow_workflow {

    name "Test Workflow TEST_WORKFLOW"
    script "test-data/test_workflow.nf"
    workflow "TEST_WORKFLOW"

    test("Should run without failures") {

        when {
            params {
                // define parameters here. Example:
                // outdir = "tests/results"
            }
            workflow {
                """
                // define inputs of the process here. Example:
                // input[0] = file("test-file.txt")
                """
            }
        }

        then {
            assert workflow.success

        }

    }

}
```
