process ONE {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number
    val name

  output:
    path "*.txt", emit: my_output_files
    tuple val(number), val(name), path("*.txt"), emit: my_output_tuple
    val number, emit: my_output_numbers

  script:
  """
  echo "one" > "${number}_${name}.txt"
  """

}


process TWO {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number
    val name

  output:
    path "*.txt", emit: my_output_files
    tuple val(number), val(name), path("*.txt"), emit: my_output_tuple
    val number, emit: my_output_numbers

  script:
  """
  echo "two" > "${number}_${name}.txt"
  """

}
