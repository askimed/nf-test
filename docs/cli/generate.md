# `generate` command

## Usage

```
nf-test generate <TEST_CASE_TYPE> <NEXTFLOW_FILES>
```

### Supported Types

#### `process`

#### `workflow`

#### `pipeline`


## Examples

Create a testcase for a process:

```
nf-test generate process modules/local/vcf_to_plink.nf
```

Create a testcases for all processes in folder `modules`:

```
nf-test generate process modules/**/*.nf
```

Create a testcase for a subworkflow:

```
nf-test generate workflow workflows/vcf_workflow.nf
```

Create a testcase for the whole pipeline:

```
nf-test generate pipeline main.nf
```
