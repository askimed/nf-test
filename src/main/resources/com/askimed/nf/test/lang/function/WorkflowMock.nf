import groovy.json.JsonGenerator
import groovy.json.JsonGenerator.Converter

nextflow.enable.dsl=2


// comes from testflight to find json files
params.nf_test_output  = ""

// function mapping
def input = []
${mapping}
//----

// include function
<% if (script != null) { %>
include { ${include} } from '${script}'
<% } %>

// define custom rules for JSON that will be generated.
def jsonOutput =
    new JsonGenerator.Options()
        .addConverter(Path) { value -> value.toAbsolutePath().toString() } // Custom converter for Path. Only filename
        .build()

def jsonWorkflowOutput = new JsonGenerator.Options().excludeNulls().build()


workflow {

  result = ${function}(*input)
  if (result != null) {
  	new File("\${params.nf_test_output}/function.json").text = jsonOutput.toJson(result)
  }
  
}


workflow.onComplete {

	def result = [
		success: workflow.success,
		exitStatus: workflow.exitStatus,
		errorMessage: workflow.errorMessage,
		errorReport: workflow.errorReport
	]
    new File("\${params.nf_test_output}/workflow.json").text = jsonWorkflowOutput.toJson(result)
    
}