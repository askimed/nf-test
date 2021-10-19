process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number
    val name

  output:
     path "*.txt", emit: my_output_files
     tuple val(number), val(name), path("*.txt"), emit: my_output_tuple

  """
  echo "lukas forer" > "${number}_${name}.txt"
  """

}
