# Files

## md5 Checksum

nf-test extends `path` by a `md5` property that can be used to compare the file content with an expected checksum:

```Groovy
assert path(process.out.out_ch.get(0)).md5 == "64debea5017a035ddc67c0b51fa84b16"
```

## JSON Files

nf-test extends `path` by a `json` property that can be used to read json files:


```Groovy
assert path(process.out.out_ch.get(0)).json.key == "value"
```

## GZip Files

nf-test extends `path` by a `linesGzip` property that can be used to read gzip compressed files:


```Groovy
assert path(process.out.out_ch.get(0)).linesGzip.size == 5
assert path(process.out.out_ch.get(0)).linesGzip.contains("Line Content")
```
