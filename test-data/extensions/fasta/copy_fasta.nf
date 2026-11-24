process COPY_FASTA {

  input:
    path fasta

  output:
    path "output.fasta", emit: fasta

  script:
  """
	cp ${fasta} output.fasta
  """

}
