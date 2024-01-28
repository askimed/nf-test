process COPY {

  input:
    path input

  output:
     path "output.gz", emit: gzip

  """
  cp ${input} output.gz
  """

}
