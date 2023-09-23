#!/usr/bin/env nextflow

process WRITE_VERSION {
    input:
        val version

    output:
        path "*.txt", emit: versions

    script:
    """
    echo ${version} > version.txt
    """
}

workflow DUPLICATE_FILENAMES {
    take: things
    main:
        WRITE_VERSION(things)
    emit:
        versions = WRITE_VERSION.out.versions
}