# Configuration

## `nf-test.config`

The `nf-test.config` file is a configuration file used to customize settings and behavior for `nf-test`. This file must be located in the root of your project, and it is automatically loaded when you run `nf-test test`. Below are the parameters that can be adapted:

| Parameter    | Description                                                                                                                                                        | Default Value             |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------| ------------------------- |
| `testsDir`   | Location for storing all nf-test cases (test scripts). If you want all test files to be in the same directory as the script itself, you can set the testDir to `.` | `"tests"`                 |
| `workDir`    | Directory for storing temporary files and working directories for each test. This directory should be added to `.gitignore`.                                       | `".nf-test"`              |
| `configFile` | Location of an optional `nextflow.config` file specifically used for executing tests. [Learn more](#testsnextflowconfig).                                          | `"tests/nextflow.config"` |
| `libDir`     | Location of a library folder that is automatically added to the classpath during testing to include additional libraries or resources needed for test cases.       | `"tests/lib"`             |
| `profile`    | Default profile to use for running tests defined in the Nextflow configuration. See [Learn more](#managing-profiles).                                              | `"docker"`                |
| `withTrace`  | Enable or disable tracing options during testing. Disable tracing if your containers don't include the `procps` tool.                                              | `true`                    |
| `autoSort`   | Enable or disable sorted channels by default when running tests.                                                                                                   | `true`                    |
| `options`    | Custom Nextflow command-line options to be applied when running tests. For example `"-dump-channels -stub-run"`                                                    |                           |
| `ignore`     | List of filenames or patterns that should be ignored when building the dependency graph. For example: `ignore 'folder/**/*.nf', 'modules/module.nf'`               | ``                        |
| `triggers`   | List of filenames or patterns that should be trigger a full test run. For example: `triggers 'nextflow.config', 'test-data/**/*'`                                  | ``                        |
| `requires`   | Can be used to specify the minimum required version of nf-test. Requires nf-test > 0.9.0                                                                           | ``                        |

Here's an example of what an `nf-test.config` file could look like:

```groovy
config {
    testsDir "tests"
    workDir ".nf-test"
    configFile "tests/nextflow.config"
    libDir "tests/lib"
    profile "docker"
    withTrace false
    autoSort false
    options "-dump-channels -stub-run"
}
```

The `requires` keyword can be used to specify the minimum required version of nf-test.
For instance, to ensure the use of at least nf-test version 0.9.0, define it as follows:

```groovy
config {
    requires (
        "nf-test": "0.9.0"
    )
}
```

## `tests/nextflow.config`

This optional `nextflow.config` file is used to execute tests. This is a good place to set default `params` for all your tests. Example number of threads:

```groovy
params {
    // run all tests with 1 threads
    threads = 1
}
```

## Configuration for tests

nf-test allows to set and overwrite the `config`, `autoSort` and `options` properties for a specific testsuite:

```
nextflow_process {

    name "Test Process..."
    script "main.nf"
    process "my_process"
    config "path/to/test/nextflow.config"
    autoSort false
    options "-dump-channels"
    ...

}
```

It is also possible to overwrite these properties for specific test. Depending on the used Nextflow option, also add the `--debug` nf-test option on the command-line to see the additional output.

```
nextflow_process {

   test("my test") {

      config "path/to/test/nextflow.config"
      autoSort false
      options "-dump-channels"
      ...

    }

}
```

## Managing Profiles

Profiles in `nf-test` provide a convenient way to configure and customize Nextflow executions for your test cases. To run your test using a specific Nextflow profile, you can use the `--profile` argument on the command line or define a default profile in `nf-test.config`.

### Basic Profile Usage

By default, `nf-test` reads the profile configuration from `nf-test.config`. If you've defined a profile called `A` in `nf-test.config`, running `nf-test --profile B` will start Nextflow with only the `B` profile. It replaces any existing profiles.

### Combining Profiles with "+"

To combine profiles, you can use the `+` prefix. For example, running `nf-test --profile +B` will start Nextflow with both `A` and `B` profiles, resulting in `-profile A,B`. This allows you to extend the existing configuration with additional profiles.

### Profile Priority Order

Profiles are evaluated in a specific order, ensuring predictable behavior:

1. **Profile in nf-test.config:** The first profile considered is the one defined in `nf-test.config`.

2. **Profile Defined in Testcase:** If you specify a profile within a testcase, it takes precedence over the one in `nf-test.config`.

3. **Profile Defined on the Command Line (CLI):** Finally, any profiles provided directly through the CLI have the highest priority and override/extends previously defined profiles.

By understanding this profile evaluation order, you can effectively configure Nextflow executions for your test cases in a flexible and organized manner.

## File Staging

!!! warning

    File Staging is obsolete since version >= 0.9.0.

The `stage` section of the `nf-test.config` file is used to define files that are needed by Nextflow in the test environment (`meta` directory). Additionally, the directories `lib`, `bin`, and `assets` are automatically staged.

### Supported Directives

#### `symlink`

This directive is used to create symbolic links (symlinks) in the test environment. Symlinks are pointers to files or directories and can be useful for creating references to data files or directories required for the test. The syntax for the `symlink` directive is as follows:

```
symlink "source_path"
```

`source_path`: The path to the source file or directory that you want to symlink.

#### `copy`

This directive is used to copy files or directories into the test environment. It allows you to duplicate files from a specified source to a location within the test environment. The syntax for the `copy` directive is as follows:

```
copy "source_path"
```

`source_path`: The path to the source file or directory that you want to copy.

### Example Usage

Here's an example of how to use the `stage` section in an `nf-test.config` file:

```groovy
config {
    ...
    stage {
        symlink "data/original_data.txt"
        copy "resources/config.yml"
    }
    ...
}
```

In this example:

- The `symlink` directive creates a symlink named "original_data.txt" in the `meta` directory pointing to the file located at "data/original_data.txt."
- The `copy` directive copies the "config.yml" file from the "resources" directory to the `meta` directory.

### Testsuite

Furthermore, it is also possible to stage files that are specific to a single testsuite:

```
nextflow_workflow {

    name "Test workflow HELLO_WORKFLOW"

    script "./hello.nf"
    workflow "HELLO_WORKFLOW"

    stage {
        symlink "test-assets/test.txt"
    }

    test("Should print out test file") {
        expect {
            assert workflow.success
        }
    }

}
```
