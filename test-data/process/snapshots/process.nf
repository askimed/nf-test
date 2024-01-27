process TEST_PROCESS {

  input:
    val name

  output:
     path "*.txt", emit: my_output_files

  """
  echo "Hello ${name}!" > output.txt
  """

}
