# Using Third-Party Libraries

nf-test supports including third party libraries (e.g. jar files ) or functions from groovy files to either extend it functionality or to avoid duplicate code and to keep the logic in test cases simple.

## Using Local Files

:octicons-tag-24: 0.7.0 ·

If nf-test detects a `lib` folder in the directory of a tescase, then it adds it automatically to the classpath.

### Examples
We have a Groovy script `MyWordUtils.groovy` that contains the following class:

```Groovy
class MyWordUtils {

    def static capitalize(String word){
      return word.toUpperCase();
    }

}
```

We can put this file in a subfolder called `lib`:

```
testcase_1
├── capitalizer.nf
├── capitalizer.test
└── lib
    └── MyWordUtils.groovy
```

The file `capitalizer.nf` contains the `CAPITALIZER` process:

```Groovy
#!/usr/bin/env nextflow
nextflow.enable.dsl=2

process CAPITALIZER {
    input:
        val cheers
    output:
        stdout emit: output
    script:
       println "$cheers".toUpperCase()
    """
    """

}
```

Next, we can use this class in the `capitalizer.nf.test` like every other class that is provided by nf-test or Groovy itself:

```Groovy
nextflow_process {

    name "Test Process CAPITALIZER"
    script "capitalizer.nf"
    process "CAPITALIZER"

    test("Should run without failures") {

        when {
            process {
                """
                input[0] = "world"
                """
            }
        }

        then {
            assert process.success
            assert process.stdout.contains(MyWordUtils.capitalize('world'))
        }

    }

}
```

If we have a project and we want to reuse libraries in multiple test cases, then we can store the class in the shared lib folder. Both test cases are now able to use `MyWordUtils`:

```
tests
├── testcase_1
    ├── hello_1.nf
    ├── hello_1.nf.test
├── testcase_2
    ├── hello_2.nf
    ├── hello_2.nf.test
└── lib
    └── MyWordUtils.groovy
```

The default location is `tests/lib`. This folder location can be changed in nf-test config file.

It is also possible to use the `--lib` parameter to add an additional folder to the classpath:

```
nf-test test tests/testcase_1/hello_1.nf.test --lib tests/mylibs
```

If multiple folders are used, the they need to be separate with a colon (like in Java or Groovy).

## Using Maven Artifcats with `@Grab`

nf-test supports the `@Grab` annotation to include third-party libraries that are available in a maven repository. As the dependency is defined as a maven artifact, there is no local copy of the jar file needed and maven enables to include an exact version as well as provides an easy update process.

### Example

The following example uses the `WordUtil` class from `commons-lang`:

```Groovy
@Grab(group='commons-lang', module='commons-lang', version='2.4')
import org.apache.commons.lang.WordUtils

nextflow_process {

    name "Test Process CAPITALIZER"
    script "capitalizer.nf"
    process "CAPITALIZER"

    test("Should run without failures") {

        when {
            process {
                """
                input[0] = "world"
                """
            }
        }

        then {
            assert process.success
            assert process.stdout.contains(WordUtils.capitalize('world'))
        }

    }

}
```
