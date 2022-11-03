process TEST_PROCESS {

  publishDir "${params.outdir}", mode: 'copy'

  input:
    val var1
    val var2
    val var3
    
  output:
    tuple val(var1), val(var2), emit: my_tuples

  script:
  
    println "var1: ${params.var1}"
    println "var2: ${params.nested.var2}"
    println "var3: ${params.nested.nested.var3}"
  
    if (var1 != params.var1){
    	error "var1: $var1 vs. ${params.var1}"
    }
    
    if (var2 != params.nested.var2){
    	error "var2: $var2 vs. ${params.nested.var2}"
    }

    if (var3 != params.nested.nested.var3){
    	error "var3: $var3 vs. ${params.nested.nested.var3}"
    }

  
    """
    """

}
