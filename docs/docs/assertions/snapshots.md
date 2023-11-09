# Snapshots
:octicons-tag-24: 0.7.0

Snapshots are a very useful tool whenever you want to make sure your output channels or output files not change unexpectedly. This feature is highly inspired by [Jest](https://jestjs.io/).

A typical snapshot test case takes a snapshot of the output channels or any other object, then compares it to a reference snapshot file stored alongside the test (`*.nf.test.snap`). The test will fail, if the two snapshots do not match: either the change is unexpected, or the reference snapshot needs to be updated to the new output of a process, workflow, pipeline or function.


## Using Snapshots

The `snapshot` keyword creates a snapshot of the object and its `match` method can then be used to check if its contains the expected data from the snap file. The following example shows how to create a snapshot of a workflow channel:

```Groovy
assert snapshot(workflow.out.channel1).match()
```

You can also create a snapshot of all output channels of a process:

```Groovy
assert snapshot(process.out).match()
```

Or a specific check on a file:

```Groovy
assert snapshot(path(process.out.get(0))).match()
```

Even the result of a function can be used:

```Groovy
assert snapshot(function.result).match()
```

The first time this test runs, nf-test creates a snapshot file. This is a json file that contains a serialized version of the provided object.

The snapshot file should be committed alongside code changes, and reviewed as part of your code review process. nf-test uses pretty-format to make snapshots human-readable during code review. On subsequent test runs, nf-test will compare the data with the previous snapshot. If they match, the test will pass. If they don't match, either the test runner found a bug in your code that should be fixed, or the implementation has changed and the snapshot needs to be updated.

## Updating Snapshots

When a snapshot test is failing due to an intentional implementation change, you can use the `--update-snapshot` flag to re-generate snapshots for all failed tests.

```
nf-test test tests/main.nf.test --update-snapshot
```

## Cleaning Obsolete Snapshots

:octicons-tag-24: 0.8.0

Over time, snapshots can become outdated, leading to inconsistencies in your testing process. To help you manage obsolete snapshots, nf-test generates a list of these obsolete keys.
This list provides transparency into which snapshots are no longer needed and can be safely removed.

Running your tests with the `--clean-snapshot`or `--wipe-snapshot` option removes the obsolete snapshots from the snapshot file.
This option is useful when you want to maintain the structure of your snapshot file but remove unused entries.
It ensures that your snapshot file only contains the snapshots required for your current tests, reducing file bloat and improving test performance.

```
nf-test test tests/main.nf.test --clean-snapshot
```

>:bulb: Obsolete snapshots can only be detected when running all tests in a test file simultaneously, and when all tests pass. If you run a single test or if tests are skipped, nf-test cannot detect obsolete snapshots.


## Constructing Complex Snapshots

It is also possible to include multiple objects into one snapshot:

```Groovy
assert snapshot(workflow.out.channel1, workflow.out.channel2).match()
```

Every object that is serializable can be included into snapshots. Therefore you can even make a snapshot of the complete workflow or process object. This includes stdout, stderr, exist status, trace etc.  and is the easiest way to create a test that checks for all of this properties:

```Groovy
assert snapshot(workflow).match()
```

You can also include output files to a snapshot (e.g. useful in pipeline tests where no channels are available):

```Groovy
assert snapshot(
    workflow,
    path("${params.outdir}/file1.txt"),
    path("${params.outdir}/file2.txt"),
    path("${params.outdir}/file3.txt")
).match()
```

By default the snapshot has the same name as the test. You can also store a snapshot under a user defined name. This enables you to use multiple snapshots in one single test and to separate them in a logical way. In the following example a workflow snapshot is created, stored under the name "workflow".

```Groovy
assert snapshot(workflow).match("workflow")
```

The next example creates a snapshot of two files and saves it under "files".

```Groovy
assert snapshot(path("${params.outdir}/file1.txt"), path("${params.outdir}/file2.txt")).match("files")
```

You can also use helper methods to add objects to snapshots. For example, you can use the `list()`method to add all files of a folder to a snapshot:

```Groovy
 assert snapshot(workflow, path(params.outdir).list()).match()
```

## File Paths

If nf-test detects a path in the snapshot it automatically replace it by a unique *fingerprint* of the file that ensures the file content is the same. The fingerprint is default the md5 sum.


## Snapshot Differences

:octicons-tag-24: 0.8.0

By default, nf-test uses the `diff` tool for comparing snapshots. It employs the following default arguments:

- `-y`: Enables side-by-side comparison mode.
- `-W 200`: Sets the maximum width for displaying the differences to 200 characters.

These default arguments are applied when no custom settings are specified.

>:bulb: If `diff`is not installed on the system, nf-test will print exepcted and found snapshots without highlighting differences.


### Customizing Diff Tool Arguments

Users have the flexibility to customize the arguments passed to the diff tool using an environment variable called `NFT_DIFF_ARGS`. This environment variable allows you to modify the way the diff tool behaves when comparing snapshots.

To customize the arguments, follow these steps:

1. Set the `NFT_DIFF_ARGS` environment variable with your desired arguments.

    ```bash
    export NFT_DIFF_ARGS="<your_custom_arguments>"
    ```

2. Run `nf-test` to perform snapshot comparison, and it will utilize the custom arguments specified in `NFT_DIFF_ARGS`.

### Changing the Diff Tool

`nf-test` not only allows you to customize the arguments but also provides the flexibility to change the diff tool itself. This can be achieved by using the environment variable `NFT_DIFF`.

#### Example: Using icdiff

As an example, you can change the diff tool to `icdiff`, which supports features like colors. To switch to `icdiff`, follow these steps:

1. Install [icdiff](https://github.com/jeffkaufman/icdiff)

2. Set the `NFT_DIFF` environment variable to `icdiff` to specify the new diff tool.

    ```bash
    export NFT_DIFF="icdiff"
    ```

3. If needed, customize the arguments for `icdiff` using `NFT_DIFF_ARGS` as explained in the previous section

    ```bash
    export NFT_DIFF_ARGS="-N --cols 200 -L expected -L observed -t"
    ```

4. Run `nf-test`, and it will use `icdiff` as the diff tool for comparing snapshots.
