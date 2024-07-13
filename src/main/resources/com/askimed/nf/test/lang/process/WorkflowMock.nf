import groovy.json.JsonGenerator
import groovy.json.JsonGenerator.Converter

nextflow.enable.dsl=2

// comes from nf-test to store json files
params.nf_test_output  = ""

// include dependencies
<% for (dependency in dependencies) { %>
include { ${dependency.name} ${dependency.hasAlias() ? " as " + dependency.alias : "" } } from '${dependency.script}'
<% } %>

// include test process
include { ${process} } from '${script}'

// define custom rules for JSON that will be generated.
def jsonOutput =
    new JsonGenerator.Options()
        .addConverter(Path) { value -> value.toAbsolutePath().toString() } // Custom converter for Path. Only filename
        .build()

def jsonWorkflowOutput = new JsonGenerator.Options().excludeNulls().build()


workflow {

    // run dependencies
    <% for (dependency in dependencies) { %>
    {
        def input = []
        ${dependency.mapping}
        ${dependency.hasAlias() ? dependency.alias : dependency.name}(*input)
    }
    <% } %>

    // process mapping
    def input = []
    ${mapping}
    //----

    //run process
    ${process}(*input)

    if (${process}.output){

        // consumes all named output channels and stores items in a json file
        for (def name in ${process}.out.getNames()) {
            serializeChannel(name, ${process}.out.getProperty(name), jsonOutput)
        }	  
      
        // consumes all unnamed output channels and stores items in a json file
        def array = ${process}.out as Object[]
        for (def i = 0; i < array.length ; i++) {
            serializeChannel(i, array[i], jsonOutput)
        }    	

    }
  
}

def serializeChannel(name, channel, jsonOutput) {
    def _name = name
    def list = [ ]
    channel.subscribe(
        onNext: {
            list.add(it)
        },
        onComplete: {
              def map = new HashMap()
              map[_name] = list
              def filename = "\${params.nf_test_output}/output_\${_name}.json"
              new File(filename).text = jsonOutput.toJson(map)		  		
        } 
    )
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
