# Files

## md5 Checksum

nf-test extends `path` by a `md5` property that can be used to compare the file content with an expected checksum:

```Groovy
assert path(process.out.out_ch.get(0)).md5 == "64debea5017a035ddc67c0b51fa84b16"
```
