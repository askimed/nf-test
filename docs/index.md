# nf-test

Simple test framework for Nextflow pipelines


[:fontawesome-solid-book: Getting Started](getting-started.md){ .md-button .md-button--primary} [:fontawesome-solid-download: Installation](installation.md){ .md-button } [:fontawesome-brands-github: Source](https://github.com/askimed/nf-test){ .md-button }

Here's what a simple script to test the [Hello World Example](https://github.com/nextflow-io/hello) might look like:

```Groovy
nextflow_pipeline {

  name "Test Hello World"
  script "nextflow-io/hello"

  test("hello world example should start 4 processes") {

    expect {
      with(workflow){
        assert success
        assert trace.taks().size() == 4
        assert "Ciao world!" in stdout
        assert "Bonjour world!" in stdout
        assert "Hello world!" in stdout
        assert "Hola world!" in stdout
      }
    }

  }

}
```
