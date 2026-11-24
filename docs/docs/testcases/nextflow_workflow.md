# Workflow Testing

nf-test also allows to test a specific workflow. Please checkout the [CLI](../cli/generate.md) to generate a workflow test.

## Syntax

```Groovy
nextflow_workflow {

    name "<NAME>"
    script "<PATH/TO/NEXTFLOW_SCRIPT.nf>"
    workflow "<WORKFLOW_NAME>"

    test("<TEST_NAME>") {

    }
}
```

> :bulb: Script paths that start with `./` or `../` are considered relative paths. These paths are resolved based on the location of the test script. Relative paths are beneficial when you want to reference files or directories located within the same directory as your test script or in a parent directory. These paths provide a convenient way to access files without specifying the entire path.


## Assertions

The `workflow` object can be used in asserts to check its status, error messages or traces.

```groovy
// workflow status
assert workflow.success
assert workflow.failed
assert workflow.exitStatus == 0

// workflow error message
assert workflow.errorReport.contains("....")

// trace
//returns a list containing succeeded tasks
assert workflow.trace.succeeded().size() == 3

//returns a list containing failed tasks
assert workflow.trace.failed().size() == 0

//returns a list containing all tasks
assert workflow.trace.tasks().size() == 3

//returns a list containing all lines from stdout
assert workflow.stdout.contains("Hello World") == 3

```

### Output Channels

The `workflow.out` object provides access to the content of all named output Channels (see Nextflow `emit`):

```groovy
// channel exists
assert workflow.out.my_channel != null

// channel contains 3 elements
assert workflow.out.my_channel.size() == 3

// first element is "hello"
assert workflow.out.my_channel.get(0) == "hello"
```

### Topic channels

Topic channels are a special type of output channel that can be used to share data between processes without explicitly connecting them in the Nextflow script.

Before you can get access to the content of a topic channel, you need to define it in the test script using the `topics` keyword. This allows you to specify which topic channels you want to access and analyze in your tests:

```groovy
nextflow_workflow {
    ...
    topics "my_topic_channel", "your_topic_channel"
    topics "look_im_a_topic"
    ...
    test("test_name") {
        topics "test_specific_topic", "another_test_specific_topic"

        when {...}
        then {...}
    }
}
```

This example will watch for the topic channels `my_topic_channel`, `your_topic_channel`, `look_im_a_topic`, `test_specific_topic` and `another_test_specific_topic`. The content of these channels can then be accessed using the `topics` object in the `then` or `expect` block:

```groovy
then {
    // topic channel exists and contains "hello"
    assert topics.my_topic_channel == "hello"

    // topic channel is not used
    assert topics.your_topic_channel == []
}
```

Topic channels function the same as the `workflow.out` object specified in the [Output channels](#output-channels) section.


## Example

### Nextflow script
Create a new file and name it `trial.nf`.

```Groovy
process sayHello {
    input:
        val cheers

    output:
        stdout emit: verbiage_ch
        path '*.txt', emit: verbiage_ch2

    script:
    """
    echo -n $cheers
    echo -n $cheers > ${cheers}.txt
    """
}

workflow trial {
    take: things
    main:
        sayHello(things)
        sayHello.out.verbiage_ch.view()
    emit:
        trial_out_ch = sayHello.out.verbiage_ch2
}

workflow {
    Channel.from('hello','nf-test') | trial
}

```

### nf-test script
Create a new file and name it `trial.nf.test`.

```Groovy
nextflow_workflow {

    name "Test Workflow Trial"
    script "trial.nf"
    workflow "trial"

    test("Should run without failures") {

        when {
            workflow {
                """
                input[0] = Channel.from('hello','nf-test')
                """
            }
        }

        then {

            assert workflow.success

            with(workflow.out.trial_out_ch) {
                assert size() == 2
                assert path(get(0)).readLines().size() == 1
                assert path(get(1)).readLines().size() == 1
                assert path(get(1)).md5 == "4a17df7a54b41a84df492da3f1bab1e3"
            }

        }

    }

}

```

### Execute test
```
nf-test init
nf-test test trial.nf.test
```
