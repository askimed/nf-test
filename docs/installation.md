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

You should see something like this:

```
🚀 nf-test 0.2.2
https://code.askimed.com/nf-test
(c) 2021 Lukas Forer and Sebastian Schoenherr

Nextflow Runtime:

      N E X T F L O W
      version 21.04.3 build 5560
      created 21-07-2021 15:09 UTC (17:09 CEST)
      cite doi:10.1038/nbt.3820
      http://nextflow.io
```

Now you are ready to write your [first testcase](getting-started.md).

### Nextflow Binary not found

If you get an error message like this, then nf-test was not able to detect your nextflow installation.

```
🚀 nf-test 0.2.2
https://code.askimed.com/nf-test
(c) 2021 Lukas Forer and Sebastian Schoenherr

Nextflow Runtime:
Error: Nextflow Binary not found. Please check if Nextflow is in a directory accessible by your $PATH variable or set $NEXTFLOW_HOME.
```

To solve this issue you have two possibilites:

- Move your Nextflow binary to a directory accessible by your `$PATH` variable.
- Set the environment variable `NEXTFLOW_HOME` to the directory that contains the Nextflow binary.

## Updating

To update an existing nf-test installtion to the latest version, run the following command:

```sh
nf-test update
```

## Manual installation

All releases are also available on [Github](https://github.com/askimed/nf-test/releases).
