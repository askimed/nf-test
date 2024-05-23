# `test` command

## Usage

```
nf-test test [<NEXTFLOW_FILES>|<SCRIPT_FOLDERS>]
```

### Optional Arguments

#### `--profile <NEXTFLOW_PROFILE>`

To run your test using a specific Nextflow profile, you can use the `--profile` argument. [Learn more](/docs/docs/configuration#managing-profiles).

#### `--dry-run`

This flag allows users to simulate the execution of tests.

#### `--verbose`

Prints out the Nextflow output during test runs.

#### `--without-trace`

The Linux tool `procps` is required to run Nextflow tracing. In case your container does not support this tool, you can also run nf-test without tracing. Please note that the `workflow.trace` are not available when running it with this flag.

#### `--tag <tag>`

Execute only tests with the provided tag. Multiple tags can be used and have to be separated by commas (e.g. `tag1,tag2`).

#### `--debug`

The debug parameter prints out debugging messages and all available output channels which can be accessed in the `then` clause.

### Output Reports

#### `--tap <filename>`

Writes test results in [TAP format](https://testanything.org) to file.

#### `--junitxml <filename>`

Writes test results in [JUnit XML format](https://junit.org/) to file, which conforms to [the standard schema](https://github.com/junit-team/junit5/blob/242f3b3ef84cfd96c9de26992588812a68cdef8b/platform-tests/src/test/resources/jenkins-junit.xsd).

#### `--csv <filename>`

Writes test results in csv file.

#### `--ci`

By default,nf-test automatically stores a new snapshot. When CI mode is activated, nf-test will fail the test instead of storing the snapshot automatically.


### Optimizing Test Execution

#### `--related-tests <files>`

Finds and executes all related tests for the provided .nf or nf.test files. Multiple files can be provided space separated.

#### `--follow-dependencies`

When this flag is set, nf-test will traverse all dependencies when the related-tests flag is set.
This option is particularly useful when you need to ensure that
all dependent tests are executed, bypassing the firewall calculation process.

#### `--only-changed`

When enabled, this parameter instructs nf-test to execute tests only for files that have been modified within the
current git working tree.

#### `--changed-since <commit_hash|branch_name>`

This parameter triggers the execution of tests related to changes made since the specifie commit.
e.g. `--changed-since HEAD^` for all changes between the HEAD and HEAD - 1.

#### `--changed-until <commit_hash|branch_name>`

This parameter initiates the execution of tests related to changes made until the specified commit hash.

#### `--graph <filename>`

Enables the export of the dependency graph as a dot file.
The dot file format is commonly used for representing graphs in graphviz and other related software.

### Sharding 

This parameter allows users to divide the execution workload into manageable chunks, which can be useful for
parallel or distributed processing.

#### `--shard <shard>`
Splits the execution into arbitrary chunks defined by the format `i/n`, where `i` denotes the index of the current
chunk and `n` represents the total number of chunks. For instance, `2/5` executes the second chunk out of five.

#### `--shard-strategy <strategy>`
Description: Specifies the strategy used to build shards when the `--shard` parameter is utilized.
Accepted values are `round-robin` or `none.`. This parameter determines the method employed to distribute workload
chunks among available resources. With the round-robin strategy, shards are distributed evenly among resources in
a cyclic manner. The none strategy implies that shards won't be distributed automatically, and it's up to the
user to manage the assignment of shards. Default value is `round-robin`.

## Examples

- Run all test scripts that can be found in the `testDir` defined in the `nf-test.config` file in the current working directory:

  ```
  nf-test test
  ```

- Run all specified test scripts and search specified directories for additional test scripts:

  ```
  nf-test test tests/modules/local/salmon_index.nf.test tests/modules/bwa_index.nf.test

  nf-test test tests/modules tests/modules/bwa_index.nf.test
  ```

- Run a specific test using its hash:

  ```
  nf-test test tests/main.nf.test@d41119e4
  ```

- Run all tests and write results to `report.tap`:

  ```
  nf-test test --tap report.tap
  ```

- Run all tests (and possible integration tests) for module `modules/module_a.nf` and `modules/module_b.nf`;

  ```
  nf-test test --related-tests modules/module_a.nf modules/module_b.nf
  ```
  
- If your project is a Git directory and you have modified files, you can run tests only for these changed files by
using the following command:

  ```
  nf-test test --only-changed
  ```
  
- If you want to test all changes made between the current state of the repository and the last commit,
you can use the following command:

  ```
  nf-test test --changed-since HEAD^
  ```

- Run only the second of four shards:

  ```
  nf-test test --shard 2/4 
  ```