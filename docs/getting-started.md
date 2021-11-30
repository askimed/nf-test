# Getting started

This guide helps you to understand the concepts of nf-test and to learn writing your first test cases. Please check if you have [installed](installation.md) nf-test properly on your computer before you start. Also, this guide assumes that you have a basic knowledge of Groovy and unit testing. The [Groovy documentation](http://groovy-lang.org/documentation.html) is the best place to learn its syntax.

## Let's get started
For this example we use the [`nf-test-examples`](https://github.com/askimed/nf-test-examples) pipeline. The included example code has been developed by the Nextflow community for a [recent paper](https://www.nature.com/articles/s41592-021-01254-9). We used the code and adapted it to the new DSL2 syntax using modules (required by nf-test). Open the terminal and clone the pipeline:

```bash
# clone nextflow pipeline
git clone https://github.com/askimed/nf-test-examples

# enter project directory
cd nf-test-examples
```

The pipeline consists of three modules (`salmon.index.nf`, `salmon_align_quant.nf`,`fastqc.nf`). We use the [`salmon.index.nf`]() process to create a test case from scratch. This process takes a reference and creates an index for it.

## Init

Before we start creating our test cases, we use the `init` command to setup nf-test:

```bash
nf-test init
```

The `init` command creates the following files in the root directory of the pipeline: `nf-test.config` and `tests/nextflow.config`. It also creates a folder `tests` which is the home of your test code.

In the [configuration](configuration.md) section you can learn more about this files.


## Create your first test

The `generate` command helps you to create skeleton test code for a Nextflow process or the whole workflow.

Here we generate a test case for the process `salmon.index.nf`:

```bash
nf-test generate process modules/local/fastqc.nf
```

This command creates a new file `tests/modules/local/salmon.index.nf` with the following content:

```groovy
nextflow_process {

    name "Test Process SALMON_INDEX"
    script "modules/local/salmon_index.nf"
    process "SALMON_INDEX"

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
              // Make assertions about the content and elements of output channels here. Example:
              // assert out_channel != null
            }
        }

    }

}

```

The `generate` command filled automatically the name, script and process of our test case as well as created a skeleton for your first `test` method. Typically you create one file per process and use different `test` methods to describe the expected behaviour of the process.

A `test` has a name, a `when` and a `then` closure. The `when` block describes the input parameters of the workflow or the process. nf-test executes the process with exactly this parameters and parses the content of the output channels. Then, it evaluates the assertions defined in the `then` block to check if content of the output channels matches your expectations.

### The `when` block

The `when` block describes the input of our process and/or the Nextflow `params`.

The `params` block is optional and is a simple map that can be used to override Nextflow's input `params`.

The `process` block is a multi-line string and you can use the `input` array to set the different inputs arguments of the process. In our example, we have only one input that expects a file. Let us update the `process` block by setting the first element of the `input` array to the path of our reference file:

```groovy
when {
    params {
        outdir = "output"
    }
    process {
        """
        // Use transcriptome.fa as first input for our process
        input[0] = file("test_data/transcriptome.fa")
        """
    }
}
```

Everything that is defined in the process block is later executed in the Nextflow script that is created automatically to test your process. Therefore, you can use every Nextflow specific functions and commands to define the values of the input array (e.g. Channels, files, paths, ...).

### The `then` block

The `then` block describes the expected output channels of the process when we execute it with the input parameters from the `when` block.

The `then` block typically contains mainly assertions to check assumptions (e.g. the size and the content of an output channel). However, this block accepts every Groovy script so you can import also third party libraries to define very specific assertions.

nf-test automatically loads all output channels of the process and all their items into a map named `process.out`. You can then use this map to formulate your assertions.

For example, in the `salmon_index` process we expect to get one process executed and 16 files created. But we also want to check the md5 sum and want to look into the actual JSON file. Let us update the `then` section with some assertions that describe our expectations:

```groovy
then {
    //check if test case succeeded
    assert process.success
    //analyze trace file
    assert process.trace.tasks().size() == 1
    with(process.out) {
      assert index
      // check if output directory has been created
      assert index.size() == 1
      // count amount of created files
      assert new File(index.get(0)).listFiles().size() == 16
      // parse info.json file
      def jsonSlurper = new JsonSlurper()
      def info = jsonSlurper.parseText(new File(index.get(0)+'/info.json').text)
      assert info.num_kmers == 375730
      assert info.seq_length == 443050
      assert path(index.get(0)+'/info.json').md5 == "80831602e2ac825e3e63ba9df5d23505"
    }
```

The items of a channel are always sorted by nf-test. This provides a deterministic order inside the channel and enables you to write reproducible tests.

## Your first test specification

You can update the name of the test method to something that gives us later a good description of our specification. When we put everything together, we get the following full working test specification:

```groovy
@Grab('org.codehaus.groovy:groovy-json:3.0.9')
import groovy.json.JsonSlurper

nextflow_process {

    name "Test Process SALMON_INDEX"
    script "modules/local/salmon_index.nf"
    process "SALMON_INDEX"

    test("Should create channel index files") {

        when {
            params {
            }
            process {
                """
                input[0] = file("test_data/transcriptome.fa")
                """
            }
        }

        then {
            //check if test case succeeded
            assert process.success
            //analyze trace file
            assert process.trace.tasks().size() == 1
            with(process.out) {
              assert index
              // check if output directory has been created
              assert index.size() == 1
              // count amount of created files
              assert new File(index.get(0)).listFiles().size() == 16
              // parse info.json file
              def jsonSlurper = new JsonSlurper()
              def info = jsonSlurper.parseText(new File(index.get(0)+'/info.json').text)
              assert info.num_kmers == 375730
              assert info.seq_length == 443050
              assert path(index.get(0)+'/info.json').md5 == "80831602e2ac825e3e63ba9df5d23505"
            }
        }
    }
}
```

## Run your first test

Now, the `test` command can be used to run your test:

```bash
nf-test test tests/modules/local/salmon_index.nf.test --profile docker
```

### Specifying profiles
In this case, the `docker` profile defined in the Nextflow pipeline is used to execute the test. The profile is set using the `--profile` parameter, but you can also define a default profile in the [configuration file](configuration.md).

Congratulations! You created you first nf-test specification.


## What's next?

- Learn how to write assertions
- Learn how to write workflow tests (integration test or e2e)
- Learn how to config nf-test
