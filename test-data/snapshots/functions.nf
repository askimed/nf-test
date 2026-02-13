import java.text.SimpleDateFormat
import java.util.Date

// Function to generate a log entry with a timestamp
def generateLogEntry(index) {
    def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    def timestamp = dateFormat.format(new Date())
    return "${timestamp} - Log entry number ${index}"
}

// Function to create a log file with multiple entries
def createLogFile(fileName, entryCount) {
    def file = new File(fileName)
    file.withWriter { writer ->
        (1..entryCount).each { index ->
            def logEntry = generateLogEntry(index)
            writer.writeLine(logEntry)
        }
    }
}

// Function to generate a CSV entry with static values
def generateCsvEntry(index) {
    def name = "Item${index}"
    def quantity = 10 + index
    def price = 1.99 + (index * 0.5)
    return "${index},${name},${quantity},${price}"
}

// Function to create a CSV file with multiple entries
def createCsvFile(fileName, entryCount) {
    def file = new File(fileName)
    file.withWriter { writer ->
        // Write the header
        writer.writeLine("ID,Name,Quantity,Price")
        // Write the entries
        (1..entryCount).each { index ->
            def csvEntry = generateCsvEntry(index)
            writer.writeLine(csvEntry)
        }
    }
}


def create_object(name) {

    def chunks = [1,2,3,4,5,6,7,8,9]
    chunks.shuffle()

    // simulate output file
    createCsvFile("output.csv", 20)

    // simulate log file
    createLogFile("output.log", 20)

    return [
        id         : "1234-5678-9101",
        status     : "SUCCESS",
        start_time : new Date(),
        end_time   : new Date(),
        chunks     : chunks,
        value      : 1.3568956165789456,
        files      : [file("output.csv"), file("output.log") ]
    ]
}