# Installation

nf-test has the same requirements as Nextflow and can be used on POSIX compatible systems like Linux or OS X. You can install nf-test using the following command:

```bash
curl -fsSL https://code.askimed.com/install/nf-test | bash
```

If you don't have curl installed, you could use wget:

```bash
wget -qO- https://code.askimed.com/install/nf-test | bash
```

It will create the `nf-test` executable file in the current directory. Optionally, move the `nf-test` file to a directory accessible by your `$PATH` variable.

Test the installation with the following command:

```sh
nf-test version
```

## Manual installation

All releases are also available on [Github](https://github.com/askimed/nf-test/releases).

## Updating

To update an existing nf-test installtion to the latest version, run the following command:

```sh
nf-test update
```
