# Using Third-Party Libraries

nf-test supports the `@Grab` annotation to include third-party libraries (available as maven artifacts) in test scripts.

## Example

```Groovy
@Grab(group='commons-lang', module='commons-lang', version='2.4')
import org.apache.commons.lang.WordUtils

nextflow_process {

    name "Test Process TEST_PROCESS"
    script "test-data/test_process.nf"
    process "TEST_PROCESS"

    test("Should run without failures") {

        when {
            params {
                outdir = "tests/results"
            }
            process {
                """
                input[0] = file("test-file.txt")
                """
            }
        }

        then {
            assert process.success
            assert process.out.out_channel.get(0) == WordUtils.capitalize('world')
        }

    }

}
```
