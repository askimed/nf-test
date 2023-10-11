process GENERATE_DATA {

  input:
    val name

  output:
     path "*.txt", emit: results

  """
  echo "hello ${name}!" > "data.txt"
  """

}
