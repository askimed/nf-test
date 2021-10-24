# Getting started

For this example we use the [`gwas-regenie`](https://github.com/genepi/gwas-regenie) pipeline. Open the terminal and clone the pipeline:

```bash
# clone nextflow pipeline
git clone https://github.com/genepi/gwas-regenie

# enter project directory
cd gwas-regenie
```

We use the [`imputed_to_plink2.nf`]() process to create a testcase from scratch. This process takes a vcf file as input and converts it into the Plink2 format.

## Init

Before we start creating our testcases, we use the `init` command to setup nf-test:

```bash
$ nf-test init
```

The `init` command creates the following files in the root directory of the pipeline: `nf-test.config` and `tests/nextflow.config`. It also creates a folder `tests` which is the home of your testcode.

In the [configuration](configuration.md) section you can learn more about this files.


## Create your first test

The `generate` command helps you to create skeleton test code for a Nextflow process or the whole workflow.

Here we generate a testcase for the process `imputed_to_plink2.nf`:

```bash
$ nf-test generate process modules/local/imputed_to_plink2.nf
```

This command creates a new file `tests/modules/local/imputed_to_plink2.nf.test` with the following content:

```groovy
nextflow_process {

  name "Test Process IMPUTED_TO_PLINK2"
  script "modules/local/imputed_to_plink2.nf"
  process "IMPUTED_TO_PLINK2"

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
            process.out.with {
              // Make assertions about the content and elements of output channels here. Example:
              // assert outdir != null
            }
        }

    }

}
```

The `generate` command filled automatically the name, script and process of our testcase as well as created a skeleton for your first `test` method. Typically you create one file per process and use different `test` methods to describe the expected behaviour of the process.

A `test` has a name, a `when` and a `then` closure. The `when` block describes the input parameters of the workflow or the process. nf-test executes the process with exactly this parameters and parses the content of the output channels. Then, it evaluates the assertions defined in the `then` block to check if content of the output channels matches your expectations.

### The `when` block

The `when` block describes the input of our process and/or the Nextflow `params`.

The `params` block is optional and is a simple map that can be used to override Nextflows input `params`.

The `process` block is a multiline string and you can use the `input` array to set the different inputs arguments of the process. In our example, we have only one input that expects a file. Let us update the `process` block by setting the first element of the `input` array to the path of a small VCF file:

```groovy
when {
    params {
        outdir = "output"
    }
    process {
        """
        // Use example.vc.gz as first input for our process
        input[0] = file("tests/input/example.vcf.gz")
        """
    }
}
```

Everything that is defined in the process block is later executed in the Nextflow script that is created automatically to test your process. Therefore, you can use every Nextflow specific functions and commands to define the values of the input array (e.g. Channels, files, ...).

### The `then` block

The `then` block describes the expected output channels of the process when we execute it with the input parameters from the `when` block.

The `then` block typically contains mainly assertions to check for example the size and the content of an output channel. However, this block accepts every Groovy script so you can import also third party libraries to define very specific assertions (e.g. a VCF-File Reader can be used to check number of variants).

nf-test automatically loads all output channels of the process and all their items into a map named `process.out`. You can then use this map to formulate your assertions.

For example, in the `imputed_to_plink2` process we expect to get an tuple that contains the filename, the pgen file, the psam file and the pvar file. Let us update the `then` section with some assertions that describe our expectations:

```groovy
then {

    // Checks if process runs without failures
    assert process.success

    //Checks if a chanell imputed_pink2 was created, has 1 element
    assert process.out.imputed_plink2
    assert process.out.imputed_plink2.size() == 1

    //Checks if first tuple contains the expected content
    assert process.out.imputed_plink2.get(0).get(0) == "example.vcf"
    assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen"  1️⃣
    assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
    assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"

    //Checks if psam file contains 500 samples and 1 header line
    def psam = process.out.imputed_plink2.get(0).get(2)
    assert file(psam).readLines().size() == 501  2️⃣

}
```

<small>
1️⃣  we use a regular expression matching to check the filename, because the channels contains always the full path to an output file.

2️⃣  since we get the full path to a file, we can open it and compare the number of lines. We can also calculate a hash of the file and compare it with an expected one. See [Assertions](assertions.md) for some examples and inspiration.
</small>

The items of a channel are always sorted by nf-test. This provides a deterministic order inside the channel and enables you to write reproducible tests.

## Your first test specification

You can update the name of the test method to something that gives us later a good description of our specification. When we put everything together, we get the following full working test specification:

```groovy
nextflow_process {

    name "Test Process IMPUTED_TO_PLINK2"
    script "modules/local/imputed_to_plink2.nf"
    process "IMPUTED_TO_PLINK2"

    test("Should create a pgen, psam and pvar file with 500 samples") {

        when {
            process {
                """
                input[0] = file("tests/input/example.vcf.gz")
                """
            }
        }

        then {
            assert process.success

            assert process.out.imputed_plink2
            assert process.out.imputed_plink2.size() == 1
            assert process.out.imputed_plink2.get(0).get(0) == "example.vcf"
            assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen"
            assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
            assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"

            def psam = process.out.imputed_plink2.get(0).get(2)
            assert file(psam).readLines().size() == 501
        }

    }

}
```

## Run your first test

Now, the `test` command can be used to run your test:

```bash
$ nf-test test tests/modules/local/imputed_to_plink2.nf.test --profile docker
```

In this case, the `docker` profile defined in the Nextflow pipeline is used to execute the test. The profile is set using the `--profile` parameter, but you can also define a default profile in the [configuration file](configuration.md).

Congratulation! You created you first nf-test specification.


## What's next?

- Learn how to write Assertions
- Learn how to write workflow tests (integration test or e2e)
- Learn how to config nf-test
