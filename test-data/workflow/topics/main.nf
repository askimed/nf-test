process TEST_TOPICS {
    input:
    val name

    output:
    tuple val(name), path("*.txt"), topic: output_tuple
    path "*.txt", topic: output_file

    script:
    """
    echo "lukas forer" > "${name}.txt"
    """
}

workflow TEST_TOPICS_WORKFLOW {
    TEST_TOPICS("Lukas")
}