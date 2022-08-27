#!/usr/bin/env nextflow
nextflow.enable.dsl=2

process sayHello {
    input:
        val cheers

    output:
        stdout emit: verbiage
        path "*.txt", emit: output_files

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
}

workflow {
    Channel.from('a','b') | trial
}