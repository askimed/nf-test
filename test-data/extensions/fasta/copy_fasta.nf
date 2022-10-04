process COPY_FASTA {

  input:
    path fasta

  output:
     path "output.fasta", emit: fasta


  """
	cp ${fasta} output.fasta
  """

}
