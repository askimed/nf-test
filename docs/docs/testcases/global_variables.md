# Global Variables

The following variables are available and can be used in `setup`, `when`, `then` and `cleanup` closures.

|Name|Description| Example |
|---|---|---|
|`baseDir` or<br/>`projectDir`| The directory where the `nf-test.config` script is located. | `mypipeline`|
|`moduleDir`| The directory where the module script is located. Also works for tesing subworkflows  | Modules: `mypipeline/modules/mymodule` \\ Subworkflows: `mypipeline/subworkflows/myworkflow` |
|`moduleTestDir`| The directory where the test script is located  | `mypipeline/tests/modules/mymodule` |
|`launchDir`| The directory where the test is run. | `mypipeline/.nf-test/tests/<test_hash>` |
|`metaDir`| The directory where all meta are located (e.g. `mock.nf`).| `mypipeline/.nf-test/tests/<test_hash>/meta` |
|`workDir`| The directory where tasks temporary files are created.| `mypipeline/.nf-test/tests/<test_hash>/work` |
|`outputDir`| An output directory in the `$launchDir` that can be used to store output files. The variable contains the absolute path. If you need a relative outpu directory see [`launchDir` example](#launchdir). | `mypipeline/.nf-test/tests/<test_hash>/output` |
|`params`| Dictionary like object holding all parameters. | |

## Examples

### `outputDir`

This variable points to the directory within the temporary test directory (`.nf-test/tests/<test-dir>/output/`). The variable can be set under params:

```Groovy
params {
    outdir = "$outputDir"
}
```

### `baseDir`

This variable points to the directory to locate the base directory of the main nf-test config. The variable can be used e.g. in the process definition to build absolute paths for input files:

```Groovy
process {
    """
    file1 = file("$baseDir/tests/input/file123.gz")
    """
}
```

### `launchDir`

This variable points to the directory where the test is executed. This can be used get access to results that are created in an relative output directory:

```Groovy
when {
    params {
        outdir = "results"
    }
}
```

```Groovy
then {
    assert path("$launchDir/results").exists()
}

```