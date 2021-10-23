# Assertions

## Use regular expressions

The operator `==~` can be used to check if a string matches a regular expression:

```
assert "/my/full/path/to/process/dir/example.vcf.pgen" ==~ ".*/example.vcf.pgen"
```


## Use `.with {}`

This assertions...

```
assert process.out.imputed_plink2
assert process.out.imputed_plink2.size() == 1
assert process.out.imputed_plink2.get(0).get(0) == "example.vcf"
assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen"
assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"
```

... can be written by using `.with{}` to improve readability:

```
assert process.out.imputed_plink2
process.out.imputed_plink2.with {
    assert size() == 1
    get(0).with {
        assert get(0) == "example.vcf"
        assert get(1) ==~ ".*/example.vcf.pgen"
        assert get(2) ==~ ".*/example.vcf.psam"
        assert get(3) ==~ ".*/example.vcf.pvar"
    }
}

```

## Use file hash

TODO: implement a `sha256` method
