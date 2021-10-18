params.lukas = null
params.forer = null
params.outdir = null

numbers = Channel.of(1..5)

if (params.outdir == null) {
  exit 1 "No outdir set"
}

process test {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    set number from numbers

  output:
     file "*.txt"

  """
  echo "lukas forer" > "${number}_${params.lukas}.txt"
  """

}
