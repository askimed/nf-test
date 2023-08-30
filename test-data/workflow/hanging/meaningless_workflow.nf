workflow PipeWf {
    take:
        inputCh

    main:
        inputCh.set { outputCh }

    emit:
        outputCh
}