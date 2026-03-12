Files

md5 Checksum

nf-test extends path by a md5 property that can be used to compare the file content with an expected checksum:

assert path(process.out.out_ch.get(0)).md5 == "64debea5017a035ddc67c0b51fa84b16"


Note that for gzip compressed files, the md5 property is calculated after gunzipping the file contents, whereas for other filetypes the md5 property is directly calculated on the file itself.

JSON Files

nf-test supports comparison of JSON files and specific fields within them. To assert that two JSON files are identical:

assert path(process.out.out_ch.get(0)).json == path('./expected_output.json').json


To verify a specific field, use dot notation. For example, if your JSON contains { "tool": "nf-test", "version": "1.0" }:

// Given a JSON output file with content: { "tool": "nf-test", "version": "1.0" }
// Use dot notation to access the value of a specific field by name:
assert path(process.out.out_ch.get(0)).json.tool == "nf-test"
assert path(process.out.out_ch.get(0)).json.version == "1.0"


YAML Files

Similarly, nf-test supports comparison for YAML files. To assert that two YAML files contain the same keys and values:

assert path(process.out.out_ch.get(0)).yaml == path('./expected_output.yaml').yaml


Individual keys can also be asserted using the same semantic logic as JSON:

// For a YAML structure like:
// process:
//   tool: nf-test
assert path(process.out.out_ch.get(0)).yaml.process.tool == "nf-test"


GZip Files

nf-test extends path with a linesGzip property to read compressed files (common in bioinformatics, like .fastq.gz).

// Check the number of lines in a compressed FASTQ
assert path(process.out.out_ch.get(0)).linesGzip.size() == 400

// Verify if a specific sequence or ID exists
assert path(process.out.out_ch.get(0)).linesGzip.contains("@read_id_001")


Filter lines

The returned array from a GZip file can also be filtered by lines to inspect specific ranges.

// Get the first 4 lines (one FASTQ record)
def record = path(process.out.gzip.get(0)).linesGzip[0..3]
assert record.size() == 4

// Check if the first line is a valid header
assert record[0].startsWith("@")


Grep lines

nf-test also provides the possibility to grep only specific lines with the advantage that only a subset of lines need to be read (especially helpful for larger files).

// Grep lines 0 to 5 efficiently
def lines = path(process.out.gzip.get(0)).grepLinesGzip(0, 5)
assert lines.size() == 6

// Verify a specific header line content
def header = path(process.out.gzip.get(0)).grepLineGzip(0)
assert header.contains("instrument_id")


Snapshot Support

The possibility of filter lines from a *.gz file can also be combined with the snapshot functionality to ensure consistency.

assert snapshot(path(process.out.gzip.get(0)).linesGzip[0]).match()
