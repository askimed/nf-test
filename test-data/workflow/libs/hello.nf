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
        sayHello(channel.of(params.name))
}
