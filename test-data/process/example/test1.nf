process test {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number

  output:
     file "*.txt"

  script:
  if (params.outdir == null) {
    exit 1 "No outdir set"
  }


  """
  echo "lukas forer ${number}" > "${number}_${params.lukas}.txt"
  """

}
