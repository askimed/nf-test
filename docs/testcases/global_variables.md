# Global Variables

## outputDir

This variable points to the directory within the test directory (`.nf-test/tests/<test-dir>/output/`). The variable can be set under params:

```Groovy
params {
    outdir = "$outputDir"
}
```

## baseDir

This variable points to the directory where the main nf-test config is located. The variable can be used e.g. in the process definition:

```Groovy
process {
    """
    f1 = file('$baseDir/tests/input/file123.gz')
    """
}
```
