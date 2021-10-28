# `nextflow_pipeline`

## Properties

### `name`
### `script`

## Example

```Groovy
nextflow_pipeline {

    name "Test Workflow test1.nf"
    script "test-data/test1.nf"

    test("Should run without failures") {

        when {
            params {
                // define parameters here. Example:
                // outdir = "tests/results"
            }
        }

        then {
            assert workflow.success
        }

    }

}

```
