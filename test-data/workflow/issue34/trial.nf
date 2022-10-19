#!/usr/bin/env nextflow
nextflow.enable.dsl=2

process sayHello {
    input:
        path files
    output:
        stdout emit: output
    script:
       println files.toFile().text
    """
    """

}

workflow hello {
    take: files
    main:
        sayHello(files)
}