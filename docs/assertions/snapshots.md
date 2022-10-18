# Snapshots
:octicons-tag-24: 0.6.3 ·
:octicons-beaker-24: Experimental

Snapshots are a very useful tool whenever you want to make sure your output channels or output files not change unexpectedly. This feature is highly inspired by [Jest](https://jestjs.io/).

A typical snapshot test case takes a snapshot of the output channels or any other object, then compares it to a reference snapshot file stored alongside the test (`*.nf.test.snap`). The test will fail if the two snapshots do not match: either the change is unexpected, or the reference snapshot needs to be updated to the new output of a process, workflow or pipeline.


## Using Snapshots

The `snapshot` keyword creates a snapshot of the object and its `match` method can then be used to check if its contains the expected data from the snap file. The following example shows how to create a snaphot of an output channel:

```
assert snapshot(workflow.out.channel1).match()
```

The first time this test is run, nf-test creates a snapshot file. This is a json file that contains a serialized version of the provided object.

The snapshot file should be committed alongside code changes, and reviewed as part of your code review process. nf-test uses pretty-format to make snapshots human-readable during code review. On subsequent test runs, nf-test will compare the rendered output with the previous snapshot. If they match, the test will pass. If they don't match, either the test runner found a bug in your code that should be fixed, or the implementation has changed and the snapshot needs to be updated.

## Updating Snapshots

When a snapshot test is failing due to an intentional implementation change, you can use the `--update-snapshot` flag to re-generate snapshots for all failed tests.

```
nf-test test tests/main.nf.test --update-snapshot
```

## More Examples

It is also possible to include multiple objects into one snapshot:

```
assert snapshot(workflow.out.channel1, workflow.out.channel2).match()
```

Every object that is serializable can be included into snapshots. Therefore you can even make a snapshot of the complete workflow or process object. This includes stdout, stderr, exist status, trace, ... and is the easiest way to create a test that checks for all of this properties:

```
assert snapshot(workflow).match()
```

You can also include output files to a snapshot (e.g. useful in pipeline tests where no channels are available):

```
assert snapshot(
    workflow,
    path("${params.outdir}/file1.txt"),
    path("${params.outdir}/file2.txt"),
    path("${params.outdir}/file3.txt")
).match()
```

As default the snapshot has the same name as the test. You can each stored snapshot also a user defined name This enables you to use multiple snapshots in one single test and to separate them in a logical way:

```
assert snapshot(workflow).match("workflow")
assert snapshot(path("${params.outdir}/file1.txt"), path("${params.outdir}/file2.txt")).match("files")
```

You can also use helper methods to add objects to snapshots. For example, you can use the `list()`method to add all files of a folder to a snapshot:

```
 assert snapshot(workflow, path(params.outdir).list()).match()
```

## File Paths

If nf-test detects a path in the snapshot it automatically replace it by a unique *fingerprint* of the file that ensures the file content is the same. The fingerprint is default the md5 sum, but it can be changed to encode the file content as base64 and use this string.

```
nextflow_pipeline {

    snapshot {
      strategy = 'md5' //or 'base64'
    }

}
```

Base64 is perfect for small files, because with this information nf-test can then decode the file content and gives you more details when a test fails. Be careful with this feature, because huge binary files will make your snapshot huge and unreadable!
