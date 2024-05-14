process exampleProc {

    storeDir "${outputDir}/exampleProc"
    input:
        tuple(
            val(outputDir),
            val(localOutputDir),
            val(someRegexString)
        )

    output:
        tuple(
            val(outputDir),
            path("example_output/example*.txt")
        )

    shell:
        """
        mkdir -p !{localOutputDir}
        echo !{someRegexString} > "!{localOutputDir}/example1.txt"
        """
}


workflow PipeWf {
    take:
        inputCh

    main:
        inputCh
            | exampleProc
            | set { outputCh }

    emit:
        outputCh
}