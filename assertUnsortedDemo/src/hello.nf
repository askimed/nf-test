#!/usr/bin/env nextflow
nextflow.enable.dsl=2
process sayHello {
    input:
        val cheers
    output:
        stdout emit: verbiage_ch
        val cheers, emit: verbiage_ch2
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