# `list` command

## Usage

`list` command provides a convenient way to list all available test cases.

```
nf-test list [<NEXTFLOW_FILES>|<SCRIPT_FOLDERS>]
```

### Optional Arguments

#### `--tags`
Print a list of all used tags.

#### `--format json`
Print the list of tests or tags as json object.

#### `--format raw`
Print the list of tests or tags as simple list without formatting.

#### `--silent`
Hide program version and header infos.

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

* List of all testcases as json:

```
nf-test list --format json --silent
["/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@69b98c67","/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@fdb6c1cc","/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@d1c219eb","/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@3c54e3cb",...]
```

* List of all testcases as unformatted ist:

```
nf-test list --format raw --silent
/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@69b98c67
/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@fdb6c1cc
/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@d1c219eb
/Users/lukfor/Development/git/nf-gwas/tests/main.nf.test@3c54e3cb
...
```

* List of all tags as json:

```
nf-test list --tags --format json --silent
["fastqc","snakemake"]
```

* List of all tags as unformatted list:

```
nf-test list --tags --format raw --silent
fastqc
snakemake
```