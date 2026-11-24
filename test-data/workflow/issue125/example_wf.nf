process exampleProc {

    storeDir "${outputDir}/exampleProc"
    input:
        tuple(
            val(outputDir),
            val(localOutputDir),
        )

    output:
        tuple(
            val(outputDir),
            path("example_output/example*.txt")
        )

    script:
        """
        mkdir -p ${localOutputDir}
        touch "${localOutputDir}/example1.txt"
        touch "${localOutputDir}/example2.txt"
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