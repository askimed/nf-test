# `nextflow_process`

## Properties

### `name`
### `script`
### `process`


## Example

```Groovy
nextflow_process {

    name "Test Process TEST_PROCESS"
    script "test-data/test_process.nf"
    process "TEST_PROCESS"

    test("Should run without failures") {

        when {
            params {
                // define parameters here. Example:
                // outdir = "tests/results"
            }
            process {
                """
                // define inputs of the process here. Example:
                // input[0] = file("test-file.txt")
                """
            }
        }

        then {
            assert process.success
            with(process.out) {
              //Make assertions about the content and elements of output channels here. Example:
              // assert out_channel != null
            }
        }

    }

}
```
