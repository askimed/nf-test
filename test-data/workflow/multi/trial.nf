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

process sayBye {
    input:
        val cheers
    output:
        stdout emit: output
    script:
       println "Bye $cheers"    
    """
    """
}

workflow hello {
    main:
        sayHello(Channel.of('lukas'))
}

workflow bye {
    main:
        sayBye(Channel.of('lukas'))
}