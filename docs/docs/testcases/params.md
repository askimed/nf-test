# Params Dictionary

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

The `params` map can also be used in the `then` block:

```Groovy
then {
    assert params.output == "output"    
}
```

## Load params from files

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

nf-test allows to combine both techniques and therefore it is possible to overwrite one or more `params` from the json file:

```Groovy
when {
    params {
        load("$baseDir/tests/params.json")
        outputDir = "new/output/path"
    }
}
```
