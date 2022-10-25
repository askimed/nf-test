process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    path input
    path input_json

  output:
     path "*.gz", emit: output_file
     path "output.json", emit: output_json

  """
  gzip -n -c ${input} > ${input}.gz
  cp ${input_json} output.json
  """

}
