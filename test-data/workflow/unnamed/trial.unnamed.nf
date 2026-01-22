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
        out1 = sayHello.out.verbiage
        out2 = sayHello.out.output_files
    emit:
        out1
        out2
}

workflow {
    channel.from('a','b') | trial
}