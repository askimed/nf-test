# Assertions

Writing test cases means formulating assumptions by using assertions. Groovy’s power assert provides a detailed output when the boolean expression validates to false. nf-test provides several extensions and commands to simplify the work with Nextflow channels.

## Using `with`

This assertions...

```groovy
assert process.out.imputed_plink2
assert process.out.imputed_plink2.size() == 1
assert process.out.imputed_plink2.get(0).get(0) == "example.vcf"
assert process.out.imputed_plink2.get(0).get(1) ==~ ".*/example.vcf.pgen"
assert process.out.imputed_plink2.get(0).get(2) ==~ ".*/example.vcf.psam"
assert process.out.imputed_plink2.get(0).get(3) ==~ ".*/example.vcf.pvar"
```

... can be written by using `with(){}` to improve readability:

```groovy
assert process.out.imputed_plink2
with(process.out.imputed_plink2) {
    assert size() == 1
    with(get(0)) {
        assert get(0) == "example.vcf"
        assert get(1) ==~ ".*/example.vcf.pgen"
        assert get(2) ==~ ".*/example.vcf.psam"
        assert get(3) ==~ ".*/example.vcf.pvar"
    }
}
```

## Using `assertAll`
`assertAll(Closure... closures)` ensures that all supplied closures do no throw exceptions. The number of failed closures is reported in the Exception message. This useful for efficient debugging
a set of test assertions in one go.

```groovy
def a = 2

assertAll(
	{ assert a==1 },
	{ a = 1/0 },
	{ assert a==2 },
	{ assert a==3 }
)
```
The output will look like this:
```groovy

assert a==1
       ||
       |false
       2

java.lang.ArithmeticException: Division by zero
Assertion failed:

assert a==3
       ||
       |false
       2

FAILED (7.106s)

  java.lang.Exception: 3 of 4 assertions failed
```