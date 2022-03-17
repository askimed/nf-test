process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    path input

  output:
     path "*.gz", emit: output_file

  """
  gzip -c ${input} > ${input}.gz
  """

}
