// comes from testflight to find json files
params.nf_test_output  = ""

// include function
<% if (script != null) { %>
include { ${include} } from '${script}'
<% } %>

workflow {
    // define custom rules for JSON that will be generated.
    def jsonOutput = createJsonOutput()
    def jsonWorkflowOutput = createJsonWorkflowOutput()

    // function mapping
    def input = []
    ${mapping}
    //----

    def functionResult = ${function}.invoke_a(input.toArray())
    if (functionResult != null) {
        new File("\${params.nf_test_output}/function.json").text = jsonOutput.toJson(functionResult)
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