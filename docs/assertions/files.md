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

nf-test extends `path` by a `linesGzip` property that can be used to read gzip compressed files:


```Groovy
assert path(process.out.out_ch.get(0)).linesGzip.size == 5
assert path(process.out.out_ch.get(0)).linesGzip.contains("Line Content")
```
