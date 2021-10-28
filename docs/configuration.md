# Configuration

## Setup test profile

To run your test using a specific Netxflow profile, you can use the `--profile` argument on the comand line or define a default profile in `nf-test.config`


##  `nf-test.config`

This config file contains settings for nftest.

```groovy
config {
    // location where nftest should save test scripts
    testsDir "tests"
    // location of a nextflow.config file specific for executing tets
    configFile "tests/nextflow.config"
    // run all test with docker profile
    profile "docker"

}
```

## `tests/nextflow.config`

This `nextflow.config` file is used to execute tests. This is a good place to set default `params` for all your tests. Example number of threads:

```groovy
params {
    // run all tests with 1 threads
    threads = 1
}
```
