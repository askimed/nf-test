# `list` command

## Usage

`list` command provides a convenient way to list all available test cases.

```
nf-test list [<NEXTFLOW_FILES>|<SCRIPT_FOLDERS>]
```

### Optional Arguments

#### `--debug`
Show debugging infos.

## Examples

* List test cases that can be found in the `testDir` defined in the `nf-test.config` file in the current working directory:

    ```
    nf-test list
    ```

* List test cases in specified test scripts and search specified directories for additional test scripts:

    ```
    nf-test list tests/modules/local/salmon_index.nf.test tests/modules/bwa_index.nf.test

    nf-test list tests/modules tests/modules/bwa_index.nf.test
    ```
