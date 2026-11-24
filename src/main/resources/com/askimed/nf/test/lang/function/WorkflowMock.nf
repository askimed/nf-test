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

    <% if (script == null) { %>
    def functionResult = ${function.tokenize('.')[0]}.invokeMethod("${function.tokenize('.')[1]}", input.toArray())
    <% } else { %>
    def functionResult = ${function}.invoke_a(input.toArray())
    <% } %>
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
    return [
        toJson: { obj ->
            def converted = convertPathsToStrings(obj)
            return groovy.json.JsonOutput.toJson(converted)
        }
    ]
}

def convertPathsToStrings(obj) {
    if (obj instanceof java.nio.file.Path) {
        return obj.toAbsolutePath().toString()
    } else if (obj instanceof Map) {
        return obj.collectEntries { k, v -> [k, convertPathsToStrings(v)] }
    } else if (obj instanceof Collection) {
        return obj.collect { it -> convertPathsToStrings(it) }
    } else {
        return obj
    }
}

def createJsonWorkflowOutput(_input = null) {
    // _input is needed because a closure is provided to all functions called in the workflow
    return [
        toJson: { obj ->
            def filtered = removeNullValues(obj)
            return groovy.json.JsonOutput.toJson(filtered)
        }
    ]
}

def removeNullValues(obj) {
    if (obj instanceof Map) {
        return obj.findAll { _k, v -> v != null }.collectEntries { k, v -> [k, removeNullValues(v)] }
    } else if (obj instanceof Collection) {
        return obj.findAll { it -> it != null }.collect { it -> removeNullValues(it) }
    } else {
        return obj
    }
}