# Regular Expressions

## Using `==~` operator

The operator `==~` can be used to check if a string matches a regular expression:

```Groovy
assert "/my/full/path/to/process/dir/example.vcf.pgen" ==~ ".*/example.vcf.pgen"
```
