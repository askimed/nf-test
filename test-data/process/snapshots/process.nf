process TEST_PROCESS {

  input:
    val name

  output:
    path "*.txt", emit: my_output_files

  script:
  """
  echo "Hello ${name}!" > output.txt
  """

}
