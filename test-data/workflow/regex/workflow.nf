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

    script:
        """
        mkdir -p !{localOutputDir}
        echo !{someRegexString} > "!{localOutputDir}/example1.txt"
        """
}


workflow PipeWf {
    take:
        inputCh

    main:
        exampleProc(inputCh)
            .set { outputCh }

    emit:
        outputCh
}