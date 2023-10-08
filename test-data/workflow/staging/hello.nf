process HELLO {

  input:
    file input

  script:
  """
  cat ${input}
  """

}

workflow HELLO_WORKFLOW {

  HELLO(
    file("${baseDir}/test-assets/test.txt")
  )

}