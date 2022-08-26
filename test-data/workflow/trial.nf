#!/usr/bin/env nextflow
nextflow.enable.dsl=2

process sayHello {
    input:
        val cheers

    output:
        stdout emit: verbiage

    script:
    """
    echo -n $cheers
    """
}

workflow trial {
    take: things
    main:
        sayHello(things)
        sayHello.out.verbiage.view()
}

workflow {
    Channel.from(['a']) | trial
}