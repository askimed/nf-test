# `generate` command

## Usage

```
nf-test generate <TEST_CASE_TYPE> <NEXTFLOW_FILES>
```

### Supported Types

#### `process`

#### `workflow`

#### `pipeline`

#### `function`

## Examples

Create a test case for a process:

```
nf-test generate process modules/local/salmon_index.nf
```

Create a test cases for all processes in folder `modules`:

```
nf-test generate process modules/**/*.nf
```

Create a test case for a sub workflow:

```
nf-test generate workflow workflows/some_workflow.nf
```

Create a test case for the whole pipeline:

```
nf-test generate pipeline main.nf
```


Create a test case for each functio in file `functions.nf`:

```
nf-test generate function functions.nf
```
