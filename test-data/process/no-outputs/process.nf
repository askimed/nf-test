process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number
    val name

  script:
  """
  echo "lukas forer" > "${number}_${name}.txt"
  """

}
