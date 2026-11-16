"""
=====================================================================================
                        LAMBDA, map(), filter(), reduce()
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
LAMBDA
    A lambda is a small, ANONYMOUS, single-EXPRESSION function.
        lambda arguments: expression
        square = lambda x: x ** 2
    - Can take any number of arguments but only ONE expression (no statements:
      no `if/else` blocks, no loops, no assignment via `=`, no multiple lines).
      A conditional EXPRESSION (ternary) is allowed: `lambda x: x if x > 0 else -x`.
    - Mainly used where a tiny throwaway function is needed inline, especially
      as the `key=` argument of sorted()/min()/max(), or inside map()/filter().
    - Anything a lambda can do, a `def` function can also do — lambdas are
      pure syntactic convenience, never required.

map(function, iterable)
    Applies `function` to every item of `iterable`, LAZILY (returns a map
    object / iterator, not a list — wrap in list() to materialize).

filter(function, iterable)
    Keeps only items where `function(item)` is truthy, LAZILY as well.
    `filter(None, iterable)` is a shortcut to drop all falsy values.

functools.reduce(function, iterable, initializer=...)
    Repeatedly applies a binary function to reduce the iterable to a single
    cumulative value: reduce(f, [1,2,3,4]) == f(f(f(1,2),3),4)
    Moved out of builtins into functools in Python 3 because explicit
    for-loops/sum()/comprehensions are usually clearer for the same job.

KEY TABLE (quick recall)
    Concept    Purpose
    def        Define a reusable, named function
    lambda     Create a short anonymous function (one expression)
    map()      Transform every item of an iterable
    filter()   Select items matching a condition
    reduce()   Fold an iterable down to a single accumulated value
    return     Send a result back to the caller

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: lambda vs def — when would you pick one over the other?
A: Use lambda for a tiny, one-off, throwaway function passed inline as an
   argument (typically `key=` or inside map/filter). Use `def` whenever the
   function needs a name for reuse/readability, multiple statements, a
   docstring, or type hints — lambdas shouldn't be assigned to a variable and
   reused; just write a `def` at that point (PEP 8 recommendation).

Q: map()/filter() vs list comprehension — which is more idiomatic Python?
A: Comprehensions are generally preferred (PEP 20 "readability counts") since
   they read naturally and avoid a separate lambda. map/filter can still be
   handy when you already have a named function (no lambda needed) to pass
   directly, e.g. `map(str.upper, words)`.

Q: Why was reduce() demoted out of Python 3's builtins?
A: Guido van Rossum felt it often produces hard-to-read code compared to an
   explicit loop or a dedicated built-in (`sum`, `max`, `min`, `any`, `all`).
   It's still available via `from functools import reduce` when genuinely
   the clearest tool (e.g. folding with a custom combiner function).

Q: Are map() and filter() lazy?
A: Yes, in Python 3 both return iterators, consumed on demand — just like a
   generator expression. Wrap in `list(...)` to force full evaluation.
"""

tasks = ["extract data", "transform data", "load data", "validate data"]

# lambda basics
square = lambda x: x ** 2
print(square(5))

is_long_name = lambda s: len(s) > 12
print(is_long_name("load data"), is_long_name("transform data"))

# sorted() with key=lambda — the most common real-world lambda usage
by_length = sorted(tasks, key=lambda t: len(t))
print("Sorted by length:", by_length)

# map() — transform every item
uppercased = list(map(lambda t: t.upper(), tasks))
print("Uppercased:", uppercased)

# equivalent, arguably more Pythonic, via comprehension
uppercased_comp = [t.upper() for t in tasks]
print("Uppercased (comprehension):", uppercased_comp)

# filter() — select matching items
long_tasks = list(filter(lambda t: len(t) > 12, tasks))
print("Long task names:", long_tasks)

# filter(None, iterable) shortcut to drop falsy values
raw = ["extract", "", None, "transform", 0, "load"]
cleaned = list(filter(None, raw))
print("Cleaned (falsy dropped):", cleaned)

# reduce() — fold down to a single value
from functools import reduce

task_lengths = [len(t) for t in tasks]
total_chars = reduce(lambda acc, n: acc + n, task_lengths, 0)
print("Total characters across all task names:", total_chars)

longest_name = reduce(lambda a, b: a if len(a) > len(b) else b, tasks)
print("Longest task name via reduce:", longest_name)
