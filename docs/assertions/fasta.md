# FASTA Files

Requires: nf-test > 0.7.0

nf-test extends `path` by a `fasta` property that can be used to read FASTA files into maps. nf-test supports also gzipped FASTA files.


## Comparing files

```Groovy
assert path('path/to/fasta1.fasta').fasta == path("path/to/fasta2.fasta'").fasta
```

## Work with individual samples

```Groovy
def sequences = path('path/to/fasta1.fasta.gz').fasta
assert "seq1" in sequences
assert !("seq8" in sequences)
assert sequences.seq1 == "AGTACGTAGTAGCTGCTGCTACGTGCGCTAGCTAGTACGTCACGACGTAGATGCTAGCTGACTCGATGC"
```
