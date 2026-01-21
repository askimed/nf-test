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
    def jsonOutput =
        new groovy.json.JsonGenerator.Options()
            .addConverter(Path) { value -> value.toAbsolutePath().toString() } // Custom converter for Path. Only filename
            .build()

    def jsonWorkflowOutput = new groovy.json.JsonGenerator.Options().excludeNulls().build()

    // run dependencies
    <% for (dependency in dependencies) { %>
    {
        def input = []
        ${dependency.mapping}
        ${dependency.hasAlias() ? dependency.alias : dependency.name}.run(input.toArray())
    }
    <% } %>

    // workflow mapping
    def input = []
    ${mapping}
    //----

    //run workflow
    ${workflow}.run(input.toArray())
    
    if (${workflow}.output){

        // consumes all named output channels and stores items in a json file
        ${workflow}.out.getNames().each { name ->
            serializeChannel(name, ${workflow}.out.getProperty(name), jsonOutput)
        }	  
    
        // consumes all unnamed output channels and stores items in a json file
        def array = ${workflow}.out as List<Object>
        def i = 0
        array.each { output ->
            serializeChannel(i, output, jsonOutput)
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


def serializeChannel(name, channel, jsonOutput) {
    def _name = name
    def list = [ ]
    channel.subscribe(
        onNext: { entry ->
            list.add(entry)
        },
        onComplete: {
            def map = new HashMap()
            map[_name] = list
            def filename = "\${params.nf_test_output}/output_\${_name}.json"
            new File(filename).text = jsonOutput.toJson(map)		  		
        } 
    )
}
