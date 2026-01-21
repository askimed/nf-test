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
        sayHello(channel.of('lukas'))
}

workflow bye {
    main:
        sayBye(channel.of('lukas'))
}