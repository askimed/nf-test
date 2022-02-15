process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val var1
    val var2
  output:
    tuple val(var1), val(var2), emit: my_tuples

  script:
  
    println "var1: $var1"
    println "params.var1: $params.var1"
  
    if (var1 != params.var1){
    	error "$var1 vs. ${params.var1}"
    }
    
    println "var2: $var2"
    println "params.var2: $params.var2"

    if (var2 != params.var2){
    	error "$var2 vs. ${params.var2}"
    }
    
    """
    touch lf.txt
    """

}
