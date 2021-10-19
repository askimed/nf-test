import groovy.json.JsonGenerator
import groovy.json.JsonGenerator.Converter

nextflow.enable.dsl=2


// comes from testflight to find json files
params.nf_testflight_output  = "json"

def input = []
//--- when ----

      input[0] = Channel.of(1..5)
      input[1] = "test"
      params.outdir = "seb7"
    
//----

// include test process
include { TEST_PROCESS } from './test-data/test_process.nf'


// define custom rules for JSON that will be generated.
def jsonOutput =
    new JsonGenerator.Options()
        .excludeNulls()  // Do not include fields with value null..
        .addConverter(Path) { value -> value.toString() } // Custom converter for Path. Only filename
        .build()


workflow {

  TEST_PROCESS(*input)

  // consumes all output channels and stores items in a json
  def channel = Channel.empty()
  for (def name in TEST_PROCESS.out.getNames()) {
      channel << tuple(name, TEST_PROCESS.out.getProperty(name))
  }

  channel.subscribe { outputTupel ->
    def sortedList = outputTupel[1] | toSortedList
    sortedList.subscribe { list ->
      def map = new HashMap()
      def outputName = outputTupel[0]
      map[outputName] = list
      new File("${params.nf_testflight_output}/output_${outputName}.json").text = jsonOutput.toJson(map)
    }
  }
  
}