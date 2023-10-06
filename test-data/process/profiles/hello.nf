process HELLO {

  input:
    val name

  script:
    println "Hello ${name}!"
  """
  """

}
