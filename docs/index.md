# Welcome to nf-test 🚀

nf-test is a simple test framework for Nextflow pipelines.


[:fontawesome-solid-book: Getting Started](getting-started.md){ .md-button .md-button--primary} [:fontawesome-solid-download: Installation](installation.md){ .md-button } [:fontawesome-brands-github: Source](https://github.com/askimed/nf-test){ .md-button }

## Test your pipeline
Let's start with a simple example to test the [Hello World](https://github.com/nextflow-io/hello) Nextflow pipeline. To run it, copy/paste the code into a text file (e.g. `hello-world.test`) and run it with `nf-test test hello-world.test`.

```Groovy
nextflow_pipeline {

  name "Test Hello World"
  script "nextflow-io/hello"

  test("hello world example should start 4 processes") {

    expect {
      with(workflow){
        assert success
        //analyze Nextflow trace file
        assert trace.tasks().size() == 4
        //Verify if strings have been written to stdout object
        assert "Ciao world!" in stdout
        assert "Bonjour world!" in stdout
        assert "Hello world!" in stdout
        assert "Hola world!" in stdout
      }
    }
  }
}
```
## Test your process
In the nf-test script below we add some basic checks to a process of a [recently published pipeline](https://github.com/GoekeLab/bioinformatics-workflows/tree/master/nextflow).

```Groovy
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
              //verify md5 checksum
              assert path(index.get(0)+'/info.json').md5 == "80831602e2ac825e3e63ba9df5d23505"
            }
        }
    }
}

```
