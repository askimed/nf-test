process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val number

  output:
     path "folder", emit: my_output_folder

  """
  mkdir folder
  echo "lukas forer" > "folder/${number}_a.txt"
  echo "lukas forer" > "folder/${number}_b.txt"
  echo "lukas forer" > "folder/${number}_c.txt"
  """

}
