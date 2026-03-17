process GENERATE_DATA {

  input:
    val name

  output:
    path "*.txt", emit: results

  script:
  """
  echo "hello ${name}!" > "data.txt"
  """

}
