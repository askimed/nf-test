process PROCESS_DATA {

  publishDir "${params.outdir}", mode: 'copy'

  input:
  	val name 
    path input

  output:
    path "output.txt", emit: results

  script:
  """
    cp ${input} output.txt
  	echo "hey ${name}!" >> "output.txt"
  """

}
