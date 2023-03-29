# `test` command

## Usage

```
nf-test test [<NEXTFLOW_FILES>|<SCRIPT_FOLDERS>]
```

### Optional Arguments

#### `--profile <NEXTFLOW_PROFILE>`

#### `--debug`
The debug parameter prints out all available output channels which can be accessed in the `then` clause.

#### `--without-trace`
The Linux tool `procps` is required to run Nextflow tracing. In case your container does not support this tool, you can also run nf-test without tracing. Please note that the `workflow.trace` are not available when running it with this flag.

#### `--tag <tag>`
Execute only tests with the provided tag. Multiple tags can be used and have to be separated by commas (e.g. `tag1,tag2`).

#### `--tap <filename>`
Writes test results in [TAP format](https://testanything.org) to file.

#### `--junitxml <filename>`
Writes test results in [JUnit XML format](https://junit.org/) to file, which conforms to [the standard schema](https://github.com/junit-team/junit5/blob/242f3b3ef84cfd96c9de26992588812a68cdef8b/platform-tests/src/test/resources/jenkins-junit.xsd).

## Examples

* Run all test scripts that can be found in the `testDir` defined in the `nf-test.config` file in the current working directory:

    ```
    nf-test test
    ```

* Run all specified test scripts and search specified directories for additional test scripts:

    ```
    nf-test test tests/modules/local/salmon_index.nf.test tests/modules/bwa_index.nf.test

    nf-test test tests/modules tests/modules/bwa_index.nf.test
    ```

* Run a specific test using its hash:

    ```
    nf-test test tests/main.nf.test@d41119e4
    ```

* Run all tests and write results to `report.tap`:

    ```
    nf-test test --tap report.tap
    ```
