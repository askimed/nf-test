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
🚀 nf-test 0.5.0
https://code.askimed.com/nf-test
(c) 2021 -2022 Lukas Forer and Sebastian Schoenherr

Nextflow Runtime:

      N E X T F L O W
      version 21.10.6 build 5660
      created 21-12-2021 16:55 UTC (17:55 CEST)
      cite doi:10.1038/nbt.3820
      http://nextflow.io

```

Now you are ready to write your [first testcase](getting-started.md).

### Nextflow Binary not found

If you get an error message like this, then nf-test was not able to detect your Nextflow installation.

```
🚀 nf-test 0.5.0
https://code.askimed.com/nf-test
(c) 2021 -2022 Lukas Forer and Sebastian Schoenherr

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

## Compiling from source
To compile nf-test from source you shall have maven installed. This will produce a `nf-test/target/nf-test.jar` file.
```
git clone git@github.com:askimed/nf-test.git
cd nf-test
mvn install
```
To use the newly compiled `nf-test.jar`, update the `nf-test` bash script that is on your PATH to point to the new `.jar` file.
First locate it with `which nf-test`, and then modify `APP_HOME` and `APP_JAR` vars at the top:
```
#!/bin/bash
APP_HOME="/PATH/TO/nf-test/target/"
APP_JAR="nf-test.jar"
APP_UPDATE_URL="https://code.askimed.com/install/nf-test"
...
```