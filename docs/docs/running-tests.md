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

nf-test provides a simple tagging mechanism that allows to execute tests by name or by tag.

#### Inclusion by tag

Tags can be defined for each testsuite or for each testcase using the new `tag` directive:

```
nextflow_process {

	name "suite 1"
	tag "tag1"

	test("test 1") {
		tag "tag2"
		tag "tag3"
		...
	}

	test("test 2") {

		tag "tag4"
		tag "tag5"
		...

	}
}
```

For example, to execute all tests with `tag2` use the following command.

```
nf-test test --tag tag2  # collects test1
```

Names are automatically added to tags. This enables to execute suits or tests directly.

```
nf-test test --tag "suite 1"  # collects test1 and test2
```

When more tags are provided, all tests that match at least one tag will be executed. Tags are also **not** case-sensitive, both lines will result the same tests.

```
nf-test test --tag tag3,tag4  # collects test1 and test2
nf-test test --tag TAG3,TAG4  # collects test1 and test2
```

#### Exclusion by tag

The `--exclude-tag` option allows the user to supply a list of tags which when matched exclude the test from running. It operates as the direct inverse of `--tag`. Exclusion takes precedence over inclusion.

For example, to execute all tests without `tag2` use the following command.

```
nf-test test --exclude-tag tag2  # collects test2
```

Adding an explicit inclusion will still not cause `test1` to run

```
nf-test test --tag tag3 --exclude-tag tag2  # collects test2
```

#### Complex tag queries

The `--tag-query` option allows the user to construct a complex query to select specific to run tests. This parameter is mutually exclusive to the `--tag` and `--exclude-tag` options.

The query language supports the following operators in order of precedence:

- `!`: `NOT`
- `&&`: `AND`
- `||`: `OR`

Parentheses can be used to group conditions.
Tags with spaces (normally test names used as a tag) can be escaped with `'` or `""`

e.g.

```
# Match all tests with tag1 + tag2 OR with tag3
--tag-query "(tag1 && tag2) || tag3"

# Match all tests with tag3 OR without tag4
--tag-query "tag3 || !tag4"

# Match tests called 'complex test' AND tag1
--tag-query "'complex test' && tag1"

# Match everything that does not have tag1 OR tag2
--tag-query "!(tag1 || tag2)"
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
