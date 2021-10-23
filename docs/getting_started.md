# Getting started

For this example we use the [`gwas-regenie`](https://github.com/genepi/gwas-regenie) pipeline. Open the terminal and clone the pipeline:

```bash
# clone nextflow pipeline
git clone https://github.com/genepi/gwas-regenie

# enter root directory
cd gwas-regenie
```

We use the [`imputed_to_plink2.nf`]() process to create a testcase from scratch. This process takes a vcf file as input and converts it into the Plink2 format.

## Init

Before we start creating our testcases, we use the `init` command to setup nftest:

```sh
$ nftest init
```

The `init` command creates the following files in the root directory of the pipeline: `nftest.config` and `tests/nextflow.config`. It also creates a folder `tests` which is the home of your testcode.

In the [configuration](configuration.md) section you can learn more about this files.


## Create your first test

The `generate` command helps us to creates skeleton test code for a Nextflow process or the whole workflow. Here we generate a testcase for the process `imputed_to_plink2.nf`:

```
$ nftest generate process modules/local/imputed_to_plink2.nf
```

This command creates a new file `tests/modules/local/imputed_to_plink2.nf.test` with the following content:

```
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

The `generate` command filled automatically the name, script and process of our testcase as well as a skeleton for your first `test` method.

Typically you create one file per process and use different `test` methods to describe the expected behaviour of the process. A `test` has a name, a `when` and a `then` closure.

The `when` block describes the input params. Then, nftest executes your process with exactly this params and runs the scripts of the `then` block which has access to the output channel of a process.

### The `when` block

The `when` block describes the input of our process or the Nextflow `params`.

The process block is a multiline string and you can use the `input` array to set the different inputs arguments of the process. In our example, we habe only one input that expects a file. Let us update the `process` block with an example file:

```groovy
process {
    """
    // Use example.vc.gz as first input for our process
    input[0] = file("tests/input/example.vcf.gz")
    """
}
```

Everything that is defined in the process block is later injected in the Nextflow script that is created automatically to test your process. Therefore, you can use every Nextflow specific functions and commands to define the values of the inputs (e.g. Channels, files, ...).

### The `then` block

The `then` block describes the expected output channels for the provided input parameters from the `when` block.

The `then`block typically contains mainly assertions to check the size and the content of an output channel. However, this block accepts every Groovy script so you can import also third party libraries to define very specific assertions  (e.g. a VCF-File Reader).

nftest automatically loads all output channels of the process and all their items into a map named `process.out`. We can then use this map to formulate our assertions.

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
    assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen" // (1)
    assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
    assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"

    //Checks if psam file contains 500 samples and 1 header line
    def psam = process.out.imputed_plink2.get(0).get(2)
    assert file(psam).readLines().size() == 501 // (2)

}
```

(1) we use a regular expression matching to check the filename, because the channels contains always to full path to an ouput file.

(2) since we have the full path to a file, we can open it and compare the number of lines. We can also calculate a hash of this file and compare it with an expected one. See [Assertions](assertions.md) for an example.

The items of a channel are always sorted by nftest after execution. This provides a deterministic order of the items and enables you to write reproducible tests.

## Our first test specification

We update the name of the test method to something that gives us later a good description of our specification. When we put everything together, we get the following script:

```
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

Now, the `test` command can be used to run our test:

```
$ nftest test tests/modules/local/imputed_to_plink2.nf.test --profile docker
```

In this case, we use the `docker` profile defined in the Nextflow pipeline to execute our test by using the `--profile` parameter. However, you can also define a default profile in the [configuration file](configuration.md) to avoid using this command line parameter.

Congratulation! You created you first nftest specification.


## What's next?

- Learn how to write Assertions
- Learn how to write workflow tests (integration test or e2e)
- Learn how to config nftest
