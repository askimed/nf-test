# Assertions

## `process` and `workflow` objects

Examples:

```groovy
// process status
assert process.success
assert process.failed
assert process.exitStatus == 0

// process error message
assert process.errorReport.contains("....")

// workflow status
assert workflow.success
assert workflow.failed
assert workflow.exitStatus == 0

// workflow error message
assert workflow.errorReport.contains("....")

// trace
//returns a list containing succeeded tasks
assert workflow.trace.succeeded().size() == 3

//returns a list containing failed tasks
assert workflow.trace.failed().size() == 0

//returns a list containing all tasks
assert workflow.trace.tasks().size() == 3
```

## Use regular expressions

The operator `==~` can be used to check if a string matches a regular expression:

```
assert "/my/full/path/to/process/dir/example.vcf.pgen" ==~ ".*/example.vcf.pgen"
```


## Use `with`

This assertions...

```groovy
assert process.out.imputed_plink2
assert process.out.imputed_plink2.size() == 1
assert process.out.imputed_plink2.get(0).get(0) == "example.vcf"
assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen"
assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"
```

... can be written by using `with(){}` to improve readability:

```groovy
assert process.out.imputed_plink2
with(process.out.imputed_plink2) {
    assert size() == 1
    with(get(0)) {
        assert get(0) == "example.vcf"
        assert get(1) ==~ ".*/example.vcf.pgen"
        assert get(2) ==~ ".*/example.vcf.psam"
        assert get(3) ==~ ".*/example.vcf.pvar"
    }
}
```

## Use file hash

nf-test extends `path` by a `md5` property that can be used to compare the file content with an expected checksum:

```groovy
assert path(process.out.out_ch.get(0)).md5 == "64debea5017a035ddc67c0b51fa84b16"
```
