# Params and Global Variables

## Params

The `params` block is optional and is a simple map that can be used to overwrite Nextflow's input `params`. The `params` block is located in the `when` block of a testcase. You can set params manually:

```Groovy
when {
    params {
        outdir = "output"
    }
}
```

It is also possible to set nested params using the same syntax as in your Nextflow script:

```Groovy
when {
    params {
        output {
          dir = "output"
        }
    }
}
```

In addition, you can load the `params` from a JSON file:

```Groovy
when {
    params {
        load("$baseDir/tests/params.json")
    }
}
```

or from a YAML file:

```Groovy
when {
    params {
        load("$baseDir/tests/params.yaml")
    }
}
```

nf-test allows to combine both techniques and therefor it is possible to overwrite one or more `params` from the json file:

```Groovy
when {
    params {
        load("$baseDir/tests/params.json")
        outputDir = "new/output/path"
    }
}
```

## Global Variables

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
