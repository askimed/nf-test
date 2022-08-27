process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number
    val name

  """
  echo "lukas forer" > "${number}_${name}.txt"
  """

}
