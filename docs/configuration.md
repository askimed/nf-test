# Configuration

## Setup test profile

To run your test using a specific Nextflow profile, you can use the `--profile` argument on the command line or define a default profile in `nf-test.config`.


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
    // location of library folder that is added automatically to the classpath
    libDir "tests/lib"    
    // run all test with the defined docker profile from the main nextflow.config
    profile "docker"
    // disable tracing options in case container does not include `procps` Linux tool.
    withTrace false
    //disable sorted channels
    autoSort false
    // add Nextflow options
    options "-dump-channels -stub-run"
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

## Configuration for tests

nf-test allows to set an additional configuration for a testsuite:

```
nextflow_process {

    name "Test Process..."
    script "main.nf"
    process "my_process"
    config "path/to/test/nextflow.config"
    autoSort false
    options "-dump-channels"
    ...

}
```

It is also possible to overwrite the `config`, `autoSort` or Nextflow properties (e.g. `options "-dump-channels"`) for a specific test. Depending on the used Nextflow option, also add the `--debug` nf-test option on the command-line to see the addtional output.  

```
nextflow_process {

   test("my test") {

      config "path/to/test/nextflow.config"
      autoSort false
      options "-dump-channels"
      ...

    }

}
```
