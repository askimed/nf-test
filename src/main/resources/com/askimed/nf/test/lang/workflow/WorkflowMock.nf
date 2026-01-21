// comes from nf-test to store json files
params.nf_test_output  = ""

// include dependencies
<% for (dependency in dependencies) { %>
include { ${dependency.name} ${dependency.hasAlias() ? " as " + dependency.alias : "" } } from '${dependency.script}'
<% } %>

// include test workflow
include { ${workflow} } from '${script}'

workflow {

    // define custom rules for JSON that will be generated.
    def jsonOutput = createJsonOutput()
    def jsonWorkflowOutput = createJsonWorkflowOutput()

    def input = []

    // run dependencies
    <% for (dependency in dependencies) { %>
    input = []
    ${dependency.mapping}
    ${dependency.hasAlias() ? dependency.alias : dependency.name}.run(input.toArray())
    <% } %>

    // workflow mapping
    input = []
    ${mapping}
    //----

    //run workflow
    ${workflow}.run(input.toArray())
    
    if (${workflow}.output){

        // consumes all named output channels and stores items in a json file
        ${workflow}.out.getNames().each { name ->
            serializeChannel(name, ${workflow}.out.getProperty(name), jsonOutput, params.nf_test_output)
        }	  
    
        // consumes all unnamed output channels and stores items in a json file
        def array = ${workflow}.out as List<Object>
        def i = 0
        array.each { output ->
            serializeChannel(i, output, jsonOutput, params.nf_test_output)
            i += 1
        }    	

    }

    workflow.onComplete = {

        def result = [
            success: workflow.success,
            exitStatus: workflow.exitStatus,
            errorMessage: workflow.errorMessage,
            errorReport: workflow.errorReport
        ]
        new File("\${params.nf_test_output}/workflow.json").text = jsonWorkflowOutput.toJson(result)
        
    }
}


def serializeChannel(name, channel, jsonOutput, outputDir) {
    def _name = name
    def list = [ ]
    channel.subscribe(
        onNext: { entry ->
            list.add(entry)
        },
        onComplete: {
            def map = new HashMap()
            map[_name] = list
            def filename = "\${outputDir}/output_\${_name}.json"
            new File(filename).text = jsonOutput.toJson(map)		  		
        } 
    )
}

def createJsonOutput(_input = null) {
    // _input is needed because a closure is provided to all functions called in the process
    return new groovy.json.JsonGenerator.Options()
        .addConverter(Path) { value -> value.toAbsolutePath().toString() } // Custom converter for Path. Only filename
        .build()
}

def createJsonWorkflowOutput(_input = null) {
    // _input is needed because a closure is provided to all functions called in the workflow
    def options = new groovy.json.JsonGenerator.Options()
    options.excludeNulls()
    return options.build()
}