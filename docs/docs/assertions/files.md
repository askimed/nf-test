# Files

## md5 Checksum

nf-test extends `path` by a `md5` property that can be used to compare the file content with an expected checksum:

```Groovy
assert path(process.out.out_ch.get(0)).md5 == "64debea5017a035ddc67c0b51fa84b16"
```

## JSON Files
nf-test supports comparison of JSON files and keys within JSON files.
To assert that two JSON files contain the same keys and values:
```Groovy
assert path(process.out.out_ch.get(0)).json == path('./some.json').json
```
Individual keys can also be asserted:

```Groovy
assert path(process.out.out_ch.get(0)).json.key == "value"
```

## YAML Files
nf-test supports comparison of YAML files and keys within YAML files.
To assert that two YAML files contain the same keys and values:
```Groovy
assert path(process.out.out_ch.get(0)).yaml == path('./some.yaml').yaml
```
Individual keys can also be asserted:

```Groovy
assert path(process.out.out_ch.get(0)).yaml.key == "value"
```

## GZip Files

nf-test extends `path` by a `linesGzip` property that can be used to read gzip compressed files.


```Groovy
assert path(process.out.out_ch.get(0)).linesGzip.size() == 5
assert path(process.out.out_ch.get(0)).linesGzip.contains("Line Content")
```


### Filter lines
The returned array can also be filtered by lines.

```Groovy
def lines = path(process.out.gzip.get(0)).linesGzip[0..5]
assert lines.size() == 6
def lines = path(process.out.gzip.get(0)).linesGzip[0]
assert lines.equals("MY_HEADER")
```

### Grep lines
nf-test also provides the possibility to grep only specific lines with the advantage that only a subset of lines need to be read (especially helpful for larger files).

```Groovy
def lines = path(process.out.gzip.get(0)).grepLinesGzip(0,5)
assert lines.size() == 6
def lines = path(process.out.gzip.get(0)).grepLineGzip(0)
assert lines.equals("MY_HEADER")
```


### Snapshot Support
The possibility of filter lines from a *.gz file can also be combined with the snapshot functionality. 

```Groovy
assert snapshot(
path(process.out.gzip.get(0)).linesGzip[0]
).match()
```