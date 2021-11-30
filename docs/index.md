# Welcome to nf-test!

nf-test is a simple test framework for Nextflow pipelines.


[:fontawesome-solid-book: Getting Started](getting-started.md){ .md-button .md-button--primary} [:fontawesome-solid-download: Installation](installation.md){ .md-button } [:fontawesome-brands-github: Source](https://github.com/askimed/nf-test){ .md-button }

Here's what a simple script to test the [Hello World Example](https://github.com/nextflow-io/hello) looks like. To run it, copy/paste the code into a text file (e.g. `hello-world.test`) and run it with `nf-test test hello-world.test`.

```Groovy
nextflow_pipeline {

  name "Test Hello World"
  script "nextflow-io/hello"

  test("hello world example should start 4 processes") {

    expect {
      with(workflow){
        assert success
        assert trace.tasks().size() == 4
        assert "Ciao world!" in stdout
        assert "Bonjour world!" in stdout
        assert "Hello world!" in stdout
        assert "Hola world!" in stdout
      }
    }

  }

}
```

Or you can even use nf-test to test Nextflow modules using a when/then pattern. In this example we test the [index creation using Salmon](https://github.com/GoekeLab/bioinformatics-workflows/tree/master/nextflow).

```Groovy
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
