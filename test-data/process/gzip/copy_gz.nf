process COPY_GZIP {

  input:
    path gzip_file

  output:
    path "output.gz", emit: gzip

  script:
  """
	cp ${gzip_file} output.gz
  """

}
