# :rocket: nf-test

> Simple test framework for Nextflow pipelines
The full documentation can be found [here](https://code.askimed.com/nf-test).

## Installation

nf-test has the same requirements as Nextflow and can be used on POSIX compatible systems like Linux or OS X. You can install nf-test using the following command:

```bash
curl -fsSL https://code.askimed.com/install/nf-test | bash
```

If you don't have curl installed, you could use wget:

```bash
wget -qO- https://code.askimed.com/install/nf-test | bash
```

It will create the `nf-test` executable file in the current directory. Optionally, move the `nf-test` file to a directory accessible by your `$PATH` variable.

### Conda

To install this package from Bioconda run the following command:

```
conda install -c bioconda nf-test
```

*Note: this recipe is not maintained by us.*

## Usage

```
nf-test test example/*.nf.test
```

The full documentation can be found [here](https://code.askimed.com/nf-test).

## Badge

Show the world your Nextflow pipeline is using nf-test:

[![nf-test](https://img.shields.io/badge/tested_with-nf--test-337ab7.svg)](https://github.com/askimed/nf-test)

```
[![nf-test](https://img.shields.io/badge/tested_with-nf--test-337ab7.svg)](https://github.com/askimed/nf-test)
```

## Contact

- Lukas Forer [@lukfor](https://twitter.com/lukfor)
- Sebastian Schönherr [@seppinho](https://twitter.com/seppinho)
