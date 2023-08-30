import groovy.json.JsonGenerator
import groovy.json.JsonGenerator.Converter

nextflow.enable.dsl=2

// comes from nf-test to store json files
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
    println "Process channel \${_name}..."
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
              println "Wrote channel \${_name} to \${filename}"  	
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
    new File("\${params.nf_test_output}/workflow.json").text = jsonOutput.toJson(result)
    
}