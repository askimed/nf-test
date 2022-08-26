# Configuration

## Setup test profile

To run your test using a specific Netxflow profile, you can use the `--profile` argument on the command line or define a default profile in `nf-test.config`.


##  `nf-test.config`

This config file contains settings for nf-test.

```groovy
config {
    // location for all nf-tests
    testsDir "tests"
    // nf-test directory including temporary files for each test
    workDir ".nf-test"
    // location of an optional nextflow.config file specific for executing tests
    configFile "tests/nextflow.config"
    // run all test with the defined docker profile from the main nextflow.config
    profile "docker"
    // disable tracing options in case container does not include `procps` Linux tool.
    withTrace = false

}
```

## `tests/nextflow.config`

This optional `nextflow.config` file is used to execute tests. This is a good place to set default `params` for all your tests. Example number of threads:

```groovy
params {
    // run all tests with 1 threads
    threads = 1
}
```
