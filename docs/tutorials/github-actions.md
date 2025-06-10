# Setup nf-test on GitHub Actions

!!! warning

    This feature requires nf-test 0.9.0 or higher

In this tutorial, we will guide you through setting up and running `nf-test` on GitHub Actions. We will start with a simple example where all tests run in a single job, then extend it to demonstrate how you can use sharding to distribute tests across multiple jobs for improved efficiency. Finally, we will show you how to run only the tests affected by the changed files using the `--changes-since` option.

By the end of this tutorial, you will have a clear understanding of how to:

1. Set up a basic CI workflow for running `nf-test` on GitHub Actions.
2. Extend the workflow to use sharding, allowing tests to run in parallel, which can significantly reduce the overall execution time.
3. Configure the workflow to run only the test cases affected by the changed files, optimizing the CI process further.

Whether you are maintaining a complex bioinformatics pipeline or a simple data analysis workflow, integrating `nf-test` with GitHub Actions will help ensure the robustness and reliability of your code. Let's get started!

## Step 1: Running nf-test

Create a file named `.github/workflows/ci-tests.yml` in your repository with the following content:

```yaml
name: nf-test CI Tests

on: [push, pull_request]

env:
  # GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  # renovate: datasource=github-releases depName=askimed/nf-test versioning=semver
  NFT_VER: "0.9.2"
  NXF_ANSI_LOG: false
  NXF_SINGULARITY_CACHEDIR: ${{ github.workspace }}/.singularity
  NXF_SINGULARITY_LIBRARYDIR: ${{ github.workspace }}/.singularity
  # renovate: datasource=github-releases depName=nextflow/nextflow versioning=semver
  NXF_VER: "24.10.2"

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - uses: actions/setup-java@c5195efecf7bdfc987ee8bae7a71cb8b11521c00 # v4
        with:
          distribution: "temurin"
          java-version: "17"

      - name: Set up Nextflow
        uses: nf-core/setup-nextflow@v2
        with:
          version: "${{ env.NXF_VERSION }}"

      - name: Set up Python
        uses: actions/setup-python@8d9ed9ac5c53483de85588cdf95a591a75ab9f55 # v5
        with:
          python-version: "3.13"

      - name: Setup apptainer
        uses: eWaterCycle/setup-apptainer@3f706d898c9db585b1d741b4692e66755f3a1b40 #v2

      - name: Set up Singularity
        shell: bash
        run: |
          mkdir -p $NXF_SINGULARITY_CACHEDIR
          mkdir -p $NXF_SINGULARITY_LIBRARYDIR

      - name: Set up nf-test
        uses: nf-core/setup-nf-test@v1
        with:
          version: "${{ env.NFT_VER }}"
          install-pdiff: true

      - name: Run nf-test
        shell: bash
        run: |
          # NFT_WORKDIR=~ \
          # NFT_DIFF=pdiff \
          # NFT_DIFF_ARGS="--line-numbers --expand-tabs=2"

          nf-test test \
            --profile=singularity \
            --tap=test.tap \
            --verbose \
            --ci

            # Save the absolute path of the test.tap file to the output
            echo "tap_file_path=$(realpath test.tap)" >> $GITHUB_OUTPUT

      - name: Generate test summary
        if: always()
        shell: bash
        run: |
          # Add header if it doesn't exist (using a token file to track this)
          if [ ! -f ".summary_header" ]; then
            echo "# 🚀 nf-test Results" >> $GITHUB_STEP_SUMMARY
            echo "" >> $GITHUB_STEP_SUMMARY
            echo "| Status | Test Name | Profile | Shard |" >> $GITHUB_STEP_SUMMARY
            echo "|:------:|-----------|---------|-------|" >> $GITHUB_STEP_SUMMARY
            touch .summary_header
          fi

          if [ -f test.tap ]; then
            while IFS= read -r line; do
              if [[ $line =~ ^ok ]]; then
                test_name="${line#ok }"
                # Remove the test number from the beginning
                test_name="${test_name#* }"
                echo "| ✅ | ${test_name} | ${{ inputs.profile }} | ${{ inputs.shard }}/${{ inputs.total_shards }} |" >> $GITHUB_STEP_SUMMARY
              elif [[ $line =~ ^not\ ok ]]; then
                test_name="${line#not ok }"
                # Remove the test number from the beginning
                test_name="${test_name#* }"
                echo "| ❌ | ${test_name} | ${{ inputs.profile }} | ${{ inputs.shard }}/${{ inputs.total_shards }} |" >> $GITHUB_STEP_SUMMARY
              fi
            done < test.tap
          else
            echo "| ⚠️ | No test results found | ${{ inputs.profile }} | ${{ inputs.shard }}/${{ inputs.total_shards }} |" >> $GITHUB_STEP_SUMMARY
          fi

      - name: Clean up
        if: always()
        shell: bash
        run: |
          sudo rm -rf /home/ubuntu/tests/
```

### Explanation:

1. **Checkout**: Uses the `actions/checkout@v2` action to check out the repository.
2. **Set up JDK 11**: Uses the `actions/setup-java@v2` action to set up Java Development Kit version 11.
3. **Setup Nextflow**: Uses the `nf-core/setup-nextflow@v1` action to install the latest-edge version of Nextflow.
4. **Install nf-test**: Downloads and installs nf-test.
5. **Run Tests**: Runs nf-test with the `--ci` flag. This activates the CI mode. Instead of automatically storing a new snapshot as per usual, it will now fail the test if no reference snapshot is available. This enables tests to fail when a snapshot file was forgotten to be committed.

### Configuration

If you're testing a pipeline and all the tests in the modules and subworkflows, you might need to change your `nf-test.config` file to use `testsDir "."` instead of `testsDir "tests"`, so that `nf-tests` looks for `main.nf.test` files in all subdirectories. Your `nf-test.config` file might look like this:

```json
config {

    testsDir "."
    workDir ".nf-test"
    configFile "tests/nextflow.config"
    profile ""

}
```

## Step 2: Extending to Use Sharding

To distribute the tests across multiple jobs, you can set up sharding. Update your workflow file as follows:

```yaml
name: CI Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        shard: [1, 2, 3, 4]
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'

      - name: Setup Nextflow latest-edge
        uses: nf-core/setup-nextflow@v1
        with:
          version: "latest-edge"

      - name: Install nf-test
        run: |
          wget -qO- https://get.nf-test.com | bash
          sudo mv nf-test /usr/local/bin/

      - name: Run Tests (Shard ${{ matrix.shard }}/${{ strategy.job-total }})
        run: nf-test test --ci --shard ${{ matrix.shard }}/${{ strategy.job-total }}
```

### Explanation of Sharding:

1. **Matrix Strategy**: The `strategy` section defines a matrix with a `shard` parameter that has four values: `[1, 2, 3, 4]`. This will create four parallel jobs, one for each shard.
2. **Run Tests with Sharding**: The `run` command for running tests is updated to `nf-test test --shard ${{ matrix.shard }}/${{ strategy.job-total }}`. This command will run the tests for the specific shard. `${{ matrix.shard }}` represents the current shard number, and `${{ strategy.job-total }}` represents the total number of shards.

## Step 3: Running Only Tests Affected by Changed Files

To optimize the workflow further, you can run only the tests that are affected by the changed files since the last commit. Update your workflow file as follows:

```yaml
name: CI Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        shard: [1, 2, 3, 4]
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'

      - name: Setup Nextflow latest-edge
        uses: nf-core/setup-nextflow@v1
        with:
          version: "latest-edge"

      - name: Install nf-test
        run: |
          wget -qO- https://get.nf-test.com | bash
          sudo mv nf-test /usr/local/bin/

      - name: Run Tests (Shard ${{ matrix.shard }}/${{ strategy.job-total }})
        run: nf-test test --ci --shard ${{ matrix.shard }}/${{ strategy.job-total }} --changed-since HEAD^
```

### Explanation of Changes:

1. **Checkout with Full History**: The `actions/checkout@v2` action is updated with `fetch-depth: 0` to fetch the full history of the repository. This is necessary for accurately determining the changes since the last commit.
2. **Run Tests with Changed Files**: The `run` command is further updated to include the `--changed-since HEAD^` option. This option ensures that only the tests affected by the changes since the previous commit are run.


## Step 4: Adapting nf-test.config to Trigger Full Test Runs

In some cases, changes to specific critical files should trigger a full test run, regardless of other changes. To configure this, you need to adapt your `nf-test.config` file.

Add the following lines to your `nf-test.config`:

```groovy
config {
    ...
    triggers 'nextflow.config', 'nf-test.config', 'test-data/**/*'
    ...
}
```

 The `triggers` directive in `nf-test.config` specifies a list of filenames or patterns that should trigger a full test run. For example:

    - `'nextflow.config'`: Changes to the main Nextflow configuration file will trigger a full test run.
    - `'nf-test.config'`: Changes to the nf-test configuration file itself will trigger a full test run.
    - `'test-data/**/*'`: Changes to any files within the `test-data` directory will trigger a full test run.

This configuration ensures that critical changes always result in a comprehensive validation of the pipeline, providing additional confidence in your CI process.

## Step 5: Additional useful Options

The `--filter` flag allows you to selectively run test cases based on their specified types. For example, you can filter tests by module, pipeline, workflow, or function. This is particularly useful when you have a large suite of tests and need to focus on specific areas of functionality. By separating multiple types with commas, you can run a customized subset of tests that match the exact criteria you're interested in, thereby saving time and resources.

The `--related-tests` flag enables you to identify and execute all tests related to the provided `.nf` or `nf.test` files. This is ideal for scenarios where you have made changes to specific files and want to ensure that only the relevant tests are run. You can provide multiple files by separating them with spaces, which makes it easy to manage and test multiple changes at once, ensuring thorough validation of your updates.

When the `--follow-dependencies` flag is set, the nf-test tool will automatically traverse and execute all tests for dependencies related to the files specified with the `--related-tests` flag. This ensures that any interdependent components are also tested, providing comprehensive coverage. This option is particularly useful for complex projects with multiple dependencies, as it bypasses the firewall calculation process and guarantees that all necessary tests are executed.

The `--changed-until` flag allows you to run tests based on changes made up until a specified commit hash or branch name. By default, this parameter uses `HEAD`, but you can specify any commit or branch to target the changes made up to that point. This is particularly useful for validating changes over a specific range of commits, ensuring that all modifications within that period are tested comprehensively.

## Summary

1. **Without Sharding**: A straightforward setup where all tests run in a single job.
2. **With Sharding**: Distributes tests across multiple jobs, allowing them to run in parallel.
3. **With Sharding and Changed Files**: Optimizes the CI process by running only the tests affected by the changed files since the last commit, in parallel jobs.

Choose the configuration that best suits your project's needs. Start with the simpler setup and extend it as needed to improve efficiency and reduce test execution time.
