# Running tests

## Basic usage
The easiest way to use nf-test is to run the following command. This command will run all tests under the `tests` directory. The `testDir` can be changed in the `nf-test.config`.

    ```
    nf-test test
    ```

## Execute specific tests
You can also specify a list of tests, which should be executed. 

    ```
    nf-test test tests/modules/local/salmon_index.nf.test tests/modules/bwa_index.nf.test

    nf-test test tests/modules tests/modules/bwa_index.nf.test
    ```

## Tag tests 

This feature provides a simple tagging mechanism that allows to execute tests by name or by tag. 

```
  	test("test 1") {
	    tag "tag2"
		tag "tag3"	 
		...
	}
```

For example, to execute all tests with `tag2` use the following command.

```
nf-test test --tag tag2  
```

Names are automatically added to tags. This enables to execute suits or tests directly. 

```
nf-test test --tag "suite 1"  # collects test1 and test2
```

When more tags are provided,  all tests that match at least one tag will be executed. Tags are also **not** case-sensitive, both lines will result the same tests.

```
nf-test test --tag tag3,tag4  # collects test1 and test2
nf-test test --tag TAG3,TAG4  # collects test1 and test2
```

## Create a TAP output

To run all tests and create a `report.tap` [file](https://testanything.org/), use the following command.

    ```
    nf-test test --tap report.tap
    ```
    

## Run test by its hash value 
To run a specific test using its hash, the following command can be used. The hash value is generated during its first execution. 

    ```
    nf-test test tests/main.nf.test@d41119e4
    ```



