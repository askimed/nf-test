# Setup Method

The setup method allows you to specify processes or workflows that need to be executed before the primary `when` block. It serves as a mechanism to prepare the required input data or set up essential steps prior to the primary processing block.

## Syntax

The setup method is typically used within the context of a test case. The basic syntax for the setup method is as follows:

```groovy
test("my test"){
    setup {
        // Define and execute dependent processes or workflows here
    }
}
```

Within the setup block, you can use the `run` method to define and execute dependent processes or workflows.

The `run` method syntax for a process is as follows:

```groovy
run("ProcessName") {
    script "path/to/process/script.nf"
    process {
        // Define the process inputs here
    }
}
```

The `run` method syntax for a workflow is as follows:

```groovy
run("WorkflowName") {
    script "path/to/workflow/script.nf"
    workflow {
        // Define the workflow inputs here
    }
}
```

!!! warning

    Please keep in mind that changes in procsses or workflows, which are executed in the setup method, can result in a failed test run.

## Example Usage

### 1. Local Setup Method

In this example, we create a setup method within a Nextflow process definition to execute a dependent process named "ABRICATE_RUN." This process generates input data that is required for the primary process "ABRICATE_SUMMARY." The `setup` block specifies the execution of "ABRICATE_RUN," and the `when` block defines the processing logic for "ABRICATE_SUMMARY."

```groovy
nextflow_process {

    name "Test process data"

    script "../main.nf"
    process "ABRICATE_SUMMARY"
    config "./nextflow.config"

    test("Should use process ABRICATE_RUN to generate input data") {

        setup {

            run("ABRICATE_RUN") {
                script "../../run/main.nf"
                process {
                    """
                    input[0] =  Channel.fromList([
                        tuple([ id:'test1', single_end:false ], // meta map
                            file(params.test_data['bacteroides_fragilis']['genome']['genome_fna_gz'], checkIfExists: true)),
                        tuple([ id:'test2', single_end:false ],
                            file(params.test_data['haemophilus_influenzae']['genome']['genome_fna_gz'], checkIfExists: true))
                    ])
                    """
                }
            }

        }

        when {
            process {
                """
                input[0] = ABRICATE_RUN.out.report.collect{ meta, report -> report }.map{ report -> [[ id: 'test_summary'], report]}
                """
            }
        }

        then {
            assert process.success
            assert snapshot(process.out).match()
        }
    }

}
```

### 2. Global Setup Method

In this example, a global setup method is defined for all tests within a Nextflow process definition. The setup method is applied to multiple test cases, ensuring consistent setup for each test. This approach is useful when multiple tests share the same setup requirements.

```groovy
nextflow_process {

    name "Test process data"

    script "../main.nf"
    process "ABRICATE_SUMMARY"
    config "./nextflow.config"

    setup {
        run("ABRICATE_RUN") {
            script "../../run/main.nf"
            process {
                """
                input[0] =  Channel.fromList([
                    tuple([ id:'test1', single_end:false ], // meta map
                        file(params.test_data['bacteroides_fragilis']['genome']['genome_fna_gz'], checkIfExists: true)),
                    tuple([ id:'test2', single_end:false ],
                        file(params.test_data['haemophilus_influenzae']['genome']['genome_fna_gz'], checkIfExists: true))
                ])
                """
            }
        }
    }

    test("first test") {
        when {
            process {
                """
                input[0] = ABRICATE_RUN.out.report.collect{ meta, report -> report }.map{ report -> [[ id: 'test_summary'], report]}
                """
            }
        }
        then {
            assert process.success
            assert snapshot(process.out).match()
        }
    }

    test("second test") {
        when {
            process {
                """
                input[0] = ABRICATE_RUN.out.report.collect{ meta, report -> report }.map{ report -> [[ id: 'test_summary'], report]}
                """
            }
        }
        then {
            assert process.success
            assert snapshot(process.out).match()
        }
    }

}
```
