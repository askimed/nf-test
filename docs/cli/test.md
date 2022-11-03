# `test` command

## Usage

```
nf-test test [<NEXTFLOW_FILES>]
```

### Optional Arguements

#### `--profile <NEXTFLOW_PROFILE>`

#### `--debug`
The debug parameter prints out all available output channels which can be accessed in the `then` clause.

#### `--without-trace`
The Linux tool `procps` is required to run Nextflow tracing. In case your container does not support this tool, you can also run nf-test without tracing. Please note that the `workflow.trace` are not available when running it with this flag.

#### `--tap <filename>`
Writes test results in [TAP format](https://testanything.org) to file.

#### `--junitxml <filename>`
Writes test results in [JUnit XML format](https://junit.org/) to file, which conforms to [the standard schema](https://github.com/junit-team/junit5/blob/242f3b3ef84cfd96c9de26992588812a68cdef8b/platform-tests/src/test/resources/jenkins-junit.xsd).

## Examples

Run all tests:

```
nf-test test
```

Run all test from a \*.test file:

```
nf-test test tests/modules/local/salmon_index.nf.test
```



Run a specific test using its hash:

```
nf-test test tests/main.nf.test@d41119e4
```

Run all tests and write results to `report.tap`:

```
nf-test test --tap report.tap
```
