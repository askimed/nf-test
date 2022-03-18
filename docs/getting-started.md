# Getting started

This guide helps you to understand the concepts of nf-test and to write your first test cases. Before you start, please check if you have [installed](installation.md) nf-test properly on your computer. Also, this guide assumes that you have a basic knowledge of Groovy and unit testing. The [Groovy documentation](http://groovy-lang.org/documentation.html) is the best place to learn its syntax.

## Let's get started
To show the power of nf-test, we adapted a recently published [proof of concept Nextflow pipeline](https://github.com/GoekeLab/bioinformatics-workflows/tree/master/nextflow). We adapted the pipeline to the new DSL2 syntax using modules (which is required by nf-test). First, open the terminal and clone our test pipeline:

```bash
# clone nextflow pipeline
git clone https://github.com/askimed/nf-test-examples

# enter project directory
cd nf-test-examples
```

The pipeline consists of three modules (`salmon.index.nf`, `salmon_align_quant.nf`,`fastqc.nf`). Here, we use the [`salmon.index.nf`]() process to create a test case from scratch. This process takes a reference as an input and creates an index using salmon.

## Init

Before creating test cases, we use the `init` command to setup nf-test.

```bash
//Init command has already been executed for our repository
nf-test init
```

The `init` command creates the following files: `nf-test.config` and the `.nf-test/tests` folder.

In the [configuration](configuration.md) section you can learn more about these files and how to customize the directory layout.


## Create your first test

The `generate` command helps you to create a skeleton test code for a Nextflow process or the complete pipeline/workflow.

Here we generate a test case for the process `salmon.index.nf`:

```bash
# delete already existing test case
rm tests/modules/local/salmon_index.nf.test
nf-test generate process modules/local/salmon_index.nf
```

This command creates a new file `tests/modules/local/salmon_index.nf` with the following content:

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

This `test` has a name, a `when` and a `then` closure (when/then closures are required here, since inputs need to be defined). The `when` block describes the input parameters of the workflow or the process. nf-test executes the process with exactly these parameters and parses the content of the output channels. Then, it evaluates the assertions defined in the `then` block to check if content of the output channels matches your expectations.

### The `when` block

The `when` block describes the input of the process and/or the Nextflow `params`.

The `params` block is optional and is a simple map that can be used to override Nextflow's input `params`.

The `process` block is a multi-line string. The `input` array can be used to set the different inputs arguments of the process. In our example, we only have one input that expects a file. Let us update the `process` block by setting the first element of the `input` array to the path of our reference file:

```groovy
when {
    params {
        outdir = "output"
    }
    process {
        """
        // Use transcriptome.fa as a first input paramter for our process
        input[0] = file("test_data/transcriptome.fa")
        """
    }
}
```

Everything which is defined in the process block is later executed in a Nextflow script (created automatically to test your process). Therefore, you can use every Nextflow specific function or command to define the values of the input array (e.g. Channels, files, paths, etc.).

### The `then` block

The `then` block describes the expected output channels of the process when we execute it with the input parameters defined in the `when` block.

The `then` block typically contains mainly assertions to check assumptions (e.g. the size and the content of an output channel). However, this block accepts every Groovy script. This means you can also import third party libraries to define very specific assertions.

nf-test automatically loads all output channels of the process and all their items into a map named `process.out`. You can then use this map to formulate your assertions.

For example, in the `salmon_index` process we expect to get one process executed and 16 files created. But we also want to check the md5 sum and want to look into the actual JSON file. Let us update the `then` section with some assertions that describe our expectations:

```groovy
then {
    //check if test case succeeded
    assert process.success
    //analyze trace file
    assert process.trace.tasks().size() == 1
    with(process.out) {
      // check if emitted output has been created
      assert index.size() == 1
      // count amount of created files
      assert path(index.get(0)).list().size() == 16
      // parse info.json file using a json parser provided by nf-test
      def info = path(index.get(0)+'/info.json').json
      assert info.num_kmers == 375730
      assert info.seq_length == 443050
      assert path(index.get(0)+'/info.json').md5 == "80831602e2ac825e3e63ba9df5d23505"
    }
}
```

The items of a channel are always sorted by nf-test. This provides a deterministic order inside the channel and enables you to write reproducible tests.

## Your first test specification

You can update the name of the test method to something that gives us later a good description of our specification. When we put everything together, we get the following full working test specification:

```groovy
nextflow_process {

    name "Test Process SALMON_INDEX"
    script "modules/local/salmon_index.nf"
    process "SALMON_INDEX"

    test("Should create channel index files") {

        when {
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
              // check if emitted output has been created
              assert index.size() == 1
              // count amount of created files
              assert path(index.get(0)).list().size() == 16
              // parse info.json file
              def info = path(index.get(0)+'/info.json').json
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
