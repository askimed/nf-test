# Files

The `path` class in **nf-test** provides several properties and methods to simplify the validation of output files within your pipeline tests.

---

## md5 Checksum

nf-test extends `path` with an `md5` property that can be used to compare the file content with an expected checksum:

```groovy
assert path(process.out.out_ch.get(0)).md5 == "64debea5017a035ddc67c0b51fa84b16"

!!! note
For gzip compressed files, the md5 property is calculated after decompressing the file contents (gunzipping). For other file types, the md5 is calculated directly on the file itself.

JSON Files

nf-test supports the comparison of JSON files and specific fields within them.

To assert that two JSON files are identical:

assert path(process.out.out_ch.get(0)).json == path('./expected_output.json').json

To verify a specific field, use dot notation. For example, if your JSON contains:

{ "tool": "nf-test", "version": "1.0" }
// Use dot notation to access the value of a specific field by name
assert path(process.out.out_ch.get(0)).json.tool == "nf-test"
assert path(process.out.out_ch.get(0)).json.version == "1.0"
YAML Files

Similarly, nf-test supports comparison for YAML files.

To assert that two YAML files contain the same keys and values:

assert path(process.out.out_ch.get(0)).yaml == path('./expected_output.yaml').yaml

Individual keys can also be asserted using the same semantic logic as JSON:

process:
  tool: nf-test
assert path(process.out.out_ch.get(0)).yaml.process.tool == "nf-test"
GZip Files

nf-test extends path with a linesGzip property to read compressed files, which are common in bioinformatics (e.g., .fastq.gz files).

// Check the number of lines in a compressed FASTQ
assert path(process.out.out_ch.get(0)).linesGzip.size() == 400

// Verify if a specific sequence or ID exists
assert path(process.out.out_ch.get(0)).linesGzip.contains("@read_id_001")
Filter Lines

The array returned from a GZip file can also be filtered by lines to inspect specific ranges.

// Get the first 4 lines (one FASTQ record)
def record = path(process.out.gzip.get(0)).linesGzip[0..3]
assert record.size() == 4

// Check if the first line is a valid header
assert record[0].startsWith("@")
Grep Lines

nf-test also allows for efficient "grepping" of specific lines, reading only a subset of the file (especially helpful for larger files).

// Grep lines 0 to 5 efficiently
def lines = path(process.out.gzip.get(0)).grepLinesGzip(0, 5)
assert lines.size() == 6

// Verify a specific header line content
def header = path(process.out.gzip.get(0)).grepLineGzip(0)
assert header.contains("instrument_id")
Snapshot Support

The ability to filter lines from a .gz file can also be combined with the Snapshot functionality to ensure consistency.

assert snapshot(path(process.out.gzip.get(0)).grepLinesGzip(0, 5)).match()
