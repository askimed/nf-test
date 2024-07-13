workflow TEST {
    take:
        myFile

    main:
        myFile.view()

    emit:
        myFile
}