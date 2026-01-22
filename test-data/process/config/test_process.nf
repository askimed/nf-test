process TEST_PROCESS {

  input:
    val number

  output:
    stdout emit: arguments

  script:
  def args = task.ext.args ?: ''
  """
  echo "${args}"
  """

}
