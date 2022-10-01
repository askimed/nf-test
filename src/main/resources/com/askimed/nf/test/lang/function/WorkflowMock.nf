import groovy.json.JsonGenerator
import groovy.json.JsonGenerator.Converter

nextflow.enable.dsl=2


// comes from testflight to find json files
params.nf_test_output  = ""

// process mapping
def input = []
${mapping}
//----

// include test process
include { ${process} } from '${script}'


// define custom rules for JSON that will be generated.
def jsonOutput =
    new JsonGenerator.Options()
        .excludeNulls()  // Do not include fields with value null..
        .addConverter(Path) { value -> value.toString() } // Custom converter for Path. Only filename
        .build()


workflow {

  result = ${process}(*input)
  new File("\${params.nf_test_output}/function.json").text = jsonOutput.toJson(result)
  
}


workflow.onComplete {

	def result = [
		success: workflow.success,
		exitStatus: workflow.exitStatus,
		errorMessage: workflow.errorMessage,
		errorReport: workflow.errorReport
	]
    new File("\${params.nf_test_output}/workflow.json").text = jsonOutput.toJson(result)
    
}