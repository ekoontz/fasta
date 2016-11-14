# How to run

overlap.scala can be compiled with:

    scalac overlap.scala

And run with:

    scala overlap <input file>

For example:

    scala overlap coding_challenge_data_set.txt

# Outline of algorithm

1. First, read in the input file into a map from
labels to sequences; call this map: _sequences_.

2. Next, iterate through the sequences. For each such sequence `a`,
find the corresponding sequence `b` where `a` and `b` overlap in the
sense that there is a substring `s` shared by `a` and `b` for which:

- `s` is a suffix of the first (left-hand) member of pair
- `s` is a prefix of the second (right-hand) member of the pair
- `s` is longer than half of both members of the pair.

As a `b` is found for each `a`, we save this correspondence in a map
called `overlaps`, keyed on `a`'s label, where the value is a pair of
`b`'s label, and an offset location within `a` where the shared
substring `s` begins within `a`.

Note that in this step, in addition to returning the pair, we also must
return the offset position from the beginning of the first member of
the pair, so that in step 3, we can print the resulting whole string
without repeating the overlapping section.

The function that, given a sequence, finds its right-hand counterpart
sequence and offset, is called `find_overlap()`.

3. Finally we print out the contents of the `overlaps` map, starting
with the leftmost sequence, printing out each such sequence in such a
way that we do not print the suffix of the sequence shared with the
sequence's right-hand counterpart. However, for the final sequence,
which has no right-hand counterpart, we simply print out its entire
contents.

How the leftmost sequence is determined is described below in the
section 'Finding the leftmost sequence'.

# How `find_overlap()` works

`find_overlap()` works as follows. For its input left-hand sequence `a`,
we consider each possible pair (`a`,`b`), where `b` is another
sequence. We look at each possible suffix in `a` that is longer than
half the length of `a`. For each such suffix, we compare it to the
prefix of `b` of the same length. If the suffix of `a` and the prefix
of `b` are equal by string equality, then `b` is the right-hand side
of the pair whose left-hand side is `a`. Having found such a `b`, we
can return from `find_overlap()` immediately, without needing to
considering any other possible `b`.

A call to `find_overlap(a)` is expensive, because we must compare
every possible suffix of `a` against every possible right hand
candidate sequence `b`. Fortunately, we can reduce the number of
possible candidates sequences `b` that we must compare against
`a`. After reading in the input file, but before any calls to
`find_overlap`, we create a set called `right_hand_candidates` which
consists of every possible sequence except the first. When we call
`find_overlap` the first time, with the first sequence in the input
file `a`, we pass along this set. `find_overlap` uses this set as the
source of possible right hand candidates. For this first call, we must
compare `a` with every other sequence, since the set
`right_hand_candidates` contains every possible sequence besides
itself. However, when `find_overlap` returns the right-hand sequence
`b`, we can remove this `b` for the set of possible
`right_hand_candidates` for all other overlaps. Thus, each call to
`find_overlap` needs to consider a smaller and smaller set of right
hand candidates.

There is one sequence for which there is no right-hand counterpart
because this sequence is at the right-hand end of the entire string
and so there is nothing to the right of it. For this string,
find_overlap returns a special tuple: `("",0)`.

# Finding the leftmost sequence

Fortunately, we don't need to do any extra work to determine the
leftmost sequence, since the leftmost sequence turns out to be the
sole remaining member of the above-mentioned `right_hand_candidates`
set. This is because the leftmost sequence is the only sequence which
is not a right-hand overlap of any other sequence.

# Evaluation code

evaluate.scala can be compiled and ran with the same argument as with overlap.scala:

    scala evaluate coding_challenge_data_set.txt

It prints out information about each sequences, from leftmost to
rightmost:

- The first 10 and last 10 characters of each sequence.
- The first 10 and last 10 characters of each shared subsequence in each overlap.
- The next sequence in the overlapping chain.

The beginning of the sample input is shown below:

```
% scala evaluate coding_challenge_data_set.txt
reading from input file:coding_challenge_data_set.txt
 length of file:51118
Rosalind_0505 [TTCATACCTC..CGGTATTAAA]
Rosalind_0505@392 is a prefix of Rosalind_9944: AAGATCGCAA...CGGTATTAAA == AAGATCGCAA...CGGTATTAAA

Rosalind_9944 [AAGATCGCAA..GGGTCGATTA]
Rosalind_9944@434 is a prefix of Rosalind_2165: TCAGCCATTA...GGGTCGATTA == TCAGCCATTA...GGGTCGATTA

Rosalind_2165 [TCAGCCATTA..TTCCACCGCA]
Rosalind_2165@388 is a prefix of Rosalind_0372: CGGACGCCGG...TTCCACCGCA == CGGACGCCGG...TTCCACCGCA
```


