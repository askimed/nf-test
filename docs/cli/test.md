# `test` command

## Usage

```
nf-test test [<NEXTFLOW_FILES>]
```

### Optional Arguements

#### `--profile <NEXTFLOW_PROFILE>`

#### `--debug`
The debug parameter prints out all available output channels which can be accessed in the `then` clause.

## Examples

Run all tests:

```
nf-test test
```

Run all test from a \*.test file:

```
nf-test test tests/modules/local/salmon_index.nf.test
```



Run a specific test using its hash:

```
nf-test test tests/main.nf.test@d41119e4
```
