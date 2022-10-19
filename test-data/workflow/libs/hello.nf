#!/usr/bin/env nextflow
nextflow.enable.dsl=2

process sayHello {
    input:
        val cheers
    output:
        stdout emit: output
    script:
       println "Hello $cheers"
    """
    """

}

workflow hello {
    main:
        sayHello(Channel.of(params.name))
}
