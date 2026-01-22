process TEST_PROCESS {

  input:
    val number

  output:
    val(task.ext.args ?: ''), emit: arguments

  script:
  """
  """

}
