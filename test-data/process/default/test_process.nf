process TEST_PROCESS {

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
  echo "lukas forer" > "${number}_${name}.txt"
  """

}
