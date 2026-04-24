workflow myWorkflow {
  input:
    val greeting
  main:
    println greeting
  output:
    stdout
}

workflow.onComplete {
  println "Workflow completed!"
}

workflow.onError {
  println "An error occurred!"
}
